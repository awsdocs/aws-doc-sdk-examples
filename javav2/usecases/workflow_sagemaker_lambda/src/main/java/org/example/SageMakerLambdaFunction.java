package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.model.OutputParameter;
import software.amazon.awssdk.services.sagemaker.model.SendPipelineExecutionStepFailureRequest;
import software.amazon.awssdk.services.sagemaker.model.SendPipelineExecutionStepSuccessRequest;
import software.amazon.awssdk.services.sagemakergeospatial.SageMakerGeospatialAsyncClient;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import software.amazon.awssdk.services.sagemakergeospatial.model.ExportVectorEnrichmentJobOutputConfig;
import software.amazon.awssdk.services.sagemakergeospatial.model.ExportVectorEnrichmentJobRequest;
import software.amazon.awssdk.services.sagemakergeospatial.model.ExportVectorEnrichmentJobResponse;
import software.amazon.awssdk.services.sagemakergeospatial.model.GetVectorEnrichmentJobRequest;
import software.amazon.awssdk.services.sagemakergeospatial.model.GetVectorEnrichmentJobResponse;
import software.amazon.awssdk.services.sagemakergeospatial.model.ReverseGeocodingConfig;
import software.amazon.awssdk.services.sagemakergeospatial.model.StartVectorEnrichmentJobRequest;
import software.amazon.awssdk.services.sagemakergeospatial.model.StartVectorEnrichmentJobResponse;
import software.amazon.awssdk.services.sagemakergeospatial.model.VectorEnrichmentJobConfig;
import software.amazon.awssdk.services.sagemakergeospatial.model.VectorEnrichmentJobDataSourceConfigInput;
import software.amazon.awssdk.services.sagemakergeospatial.model.VectorEnrichmentJobDocumentType;
import software.amazon.awssdk.services.sagemakergeospatial.model.VectorEnrichmentJobInputConfig;
import software.amazon.awssdk.services.sagemakergeospatial.model.VectorEnrichmentJobS3Data;
import software.amazon.awssdk.services.sagemakergeospatial.model.VectorEnrichmentJobStatus;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;

// The AWS Lambda function handler for the Amazon SageMaker pipeline.
public class SageMakerLambdaFunction implements RequestHandler<HashMap<String, Object>,  Map<String, String>>{
    @Override
    public Map<String, String> handleRequest(HashMap<String, Object> requestObject, Context context) throws RuntimeException {
        LambdaLogger logger = context.getLogger();
        Region region = Region.US_WEST_2;

        SageMakerClient sageMakerClient = SageMakerClient.builder()
            .region(region)
            .build();

        SageMakerGeospatialAsyncClient asyncClient = SageMakerGeospatialAsyncClient.builder()
            .region(region)
            .build();
        Gson gson = new Gson();
        if (requestObject == null) {
            logger.log("*** Request is Null");
        } else {
            logger.log("*** Request is NOT Null");
            logger.log("*** REQUEST: " + requestObject);
        }

        // Log out the values from the request. The request object is a HashMap.
        logger.log("*** vej_export_config: "+ requestObject.get("vej_export_config"));
        logger.log("*** vej_name: "+ requestObject.get("vej_name"));
        logger.log("*** vej_config: "+ requestObject.get("vej_config"));
        logger.log("*** vej_input_config: "+ requestObject.get("vej_input_config"));
        logger.log("*** role: "+ requestObject.get("role"));

        // The Records array will be populated if this request came from the queue.
        logger.log("*** records: "+ requestObject.get("Records"));

        // The response dictionary.
        Map<String, String> responseDictionary = new HashMap<>();

        if (requestObject.get("Records") != null ) {
            logger.log("Records found, this is a queue event. Processing the queue records.");
            ArrayList<HashMap<String, String>> queueMessages = (ArrayList<HashMap<String, String>>)requestObject.get("Records");
            for (HashMap<String, String> message : queueMessages) {
                processMessage(asyncClient, sageMakerClient, message.get("body"), context);
            }
        }  else if (requestObject.get("vej_export_config") != null) {
            logger.log("*** Export configuration found. Start the Vector Enrichment Job (VEJ) export.");

            JSONObject jsonObject = null;
            JSONParser parser = new JSONParser();

            try {
                jsonObject = (JSONObject) parser.parse((String)requestObject.get("vej_export_config"));
            } catch (ParseException e) {
                throw new RuntimeException("Problem parsing export config.");
            }

            JSONObject s3Data = (JSONObject) jsonObject.get("S3Data");
            String s3Uri = (String) s3Data.get("S3Uri");
            System.out.println("**** NEW S3URI: " + s3Uri);


            VectorEnrichmentJobS3Data jobS3Data = VectorEnrichmentJobS3Data.builder()
                .s3Uri(s3Uri)
                .build();

            ExportVectorEnrichmentJobOutputConfig jobOutputConfig = ExportVectorEnrichmentJobOutputConfig.builder()
                .s3Data(jobS3Data)
                .build();

            ExportVectorEnrichmentJobRequest exportRequest = ExportVectorEnrichmentJobRequest.builder()
                .arn((String)requestObject.get("vej_arn"))
                .executionRoleArn((String)requestObject.get("role"))
                .outputConfig(jobOutputConfig)
                .build();

            CompletableFuture<ExportVectorEnrichmentJobResponse> futureResponse = asyncClient.exportVectorEnrichmentJob(exportRequest);
            futureResponse.whenComplete((response, exception) -> {
                logger.log("*** IN whenComplete BLOCK");
                if (exception != null) {
                    // Handle the exception here
                    LambdaLogger logger2 = context.getLogger();
                    logger2.log("Error occurred during the asynchronous operation: " + exception.getMessage());
                } else {
                    // Process the response here
                    LambdaLogger logger2 = context.getLogger();
                    logger2.log("Export response: " + response.toString());
                    responseDictionary.put("export_eoj_status", response.exportStatusAsString());
                    responseDictionary.put("vej_arn", response.arn());
                }
            });

            /*
            By adding futureResponse.join(),
            you ensure that the main thread will wait for the CompletableFuture to complete before the Lambda function terminates. This should allow the "whenComplete" block to be executed properly and reach the Complete When block.
             */
            futureResponse.join();
        } else if (requestObject.get("vej_name") != null ) {
            logger.log("*** NEW Vector Enrichment Job name found, starting the job.");

            JSONObject jsonObject = null;
            JSONParser parser = new JSONParser();

            try {
                jsonObject = (JSONObject) parser.parse((String)requestObject.get("vej_input_config"));
            } catch (ParseException e) {
                throw new RuntimeException("Problem parsing input config.");
            }

            JSONObject dataSourceConfig = (JSONObject) jsonObject.get("DataSourceConfig");
            JSONObject s3Data = (JSONObject) dataSourceConfig.get("S3Data");
            String s3Uri = (String) s3Data.get("S3Uri");
            System.out.println("**** NEW S3URI: " + s3Uri);

            VectorEnrichmentJobS3Data s3DataOb = VectorEnrichmentJobS3Data.builder()
                .s3Uri(s3Uri)
                .build();

            VectorEnrichmentJobInputConfig inputConfig = VectorEnrichmentJobInputConfig.builder()
                .documentType(VectorEnrichmentJobDocumentType.CSV)
                .dataSourceConfig(VectorEnrichmentJobDataSourceConfigInput.fromS3Data(s3DataOb))
                .build();

            ReverseGeocodingConfig geocodingConfig = ReverseGeocodingConfig.builder()
                .xAttributeName("Longitude")
                .yAttributeName("Latitude")
                .build();

            VectorEnrichmentJobConfig jobConfig = VectorEnrichmentJobConfig.builder()
                .reverseGeocodingConfig(geocodingConfig)
                .build();

            StartVectorEnrichmentJobRequest jobRequest = StartVectorEnrichmentJobRequest.builder()
                .inputConfig(inputConfig)
                .executionRoleArn((String)requestObject.get("role"))
                .name((String)requestObject.get("vej_name"))
                .jobConfig(jobConfig)
                .build();

            logger.log("*** INVOKE geoSpatialClient.startVectorEnrichmentJob with asyncClient");
            CompletableFuture<StartVectorEnrichmentJobResponse> futureResponse = asyncClient.startVectorEnrichmentJob(jobRequest);
            futureResponse.whenComplete((response, exception) -> {
                logger.log("*** IN whenComplete BLOCK");
                if (exception != null) {
                    // Handle the exception here
                    logger.log("Error occurred during the asynchronous operation: " + exception.getMessage());
                } else {
                    // Process the response here
                    logger.log("Asynchronous job started successfully. Job Status is: " + response.toString());
                    String vej_arnValue = response.arn();
                    logger.log("vej_arn: " + vej_arnValue);
                    String status = response.statusAsString();
                    logger.log("STATUS: " + status);

                    responseDictionary.put("statusCode", status);
                    responseDictionary.put("vej_arn", vej_arnValue);
                }
            });

            /*
            By adding futureResponse.join(),
            you ensure that the main thread will wait for the CompletableFuture to complete before the Lambda function terminates. This should allow the "whenComplete" block to be executed properly and reach the Complete When block.
             */
            futureResponse.join();
            logger.log("*** OUT OF whenComplete BLOCK");
        }
        logger.log("Returning:" + responseDictionary);
        return responseDictionary;
    }

    private void processMessage(SageMakerGeospatialAsyncClient asyncClient, SageMakerClient sageMakerClient, String messageBody, Context context ) throws RuntimeException {
        Gson gson = new Gson();
        LambdaLogger logger = context.getLogger();
        logger.log("Processing message with body:" + messageBody);

        QueuePayload queuePayload =  gson.fromJson(messageBody, QueuePayload.class);
        String token = queuePayload.getToken();
        logger.log("Payload token " + token);

        if (queuePayload.getArguments().containsKey("vej_arn")) {
            // Use the job ARN and the token to get the job status.
            String job_arn = queuePayload.getArguments().get("vej_arn");
            logger.log("Token: " + token + ", arn " + job_arn);

            GetVectorEnrichmentJobRequest jobInfoRequest = GetVectorEnrichmentJobRequest.builder()
                .arn(job_arn)
                .build();

            CompletableFuture<GetVectorEnrichmentJobResponse> futureResponse = asyncClient.getVectorEnrichmentJob(jobInfoRequest);

            /*
            By adding futureResponse.join(),
            you ensure that the main thread will wait for the CompletableFuture to complete before the Lambda function terminates. This should allow the "whenComplete" block to be executed properly and reach the Complete When block.
             */
            GetVectorEnrichmentJobResponse jobResponse = futureResponse.join();

            logger.log("Job info: " + jobResponse.toString());

            if (jobResponse.status().equals(VectorEnrichmentJobStatus.COMPLETED)) {
                logger.log("Status completed, resuming pipeline...");

                OutputParameter out = OutputParameter.builder()
                    .name("export_status")
                    .value(String.valueOf(jobResponse.status()))
                    .build();

                SendPipelineExecutionStepSuccessRequest successRequest = SendPipelineExecutionStepSuccessRequest.builder()
                    .callbackToken(token)
                    .outputParameters(Collections.singletonList(out))
                    .build();

                sageMakerClient.sendPipelineExecutionStepSuccess(successRequest);

            } else if (jobResponse.status().equals(VectorEnrichmentJobStatus.FAILED)) {
                logger.log("Status failed, stopping pipeline...");
                SendPipelineExecutionStepFailureRequest failureRequest = SendPipelineExecutionStepFailureRequest.builder()
                    .callbackToken(token)
                    .failureReason(jobResponse.errorDetails().errorMessage())
                    .build();

                sageMakerClient.sendPipelineExecutionStepFailure(failureRequest);

            } else if (jobResponse.status().equals(VectorEnrichmentJobStatus.IN_PROGRESS)) {
                // Put this message back in the queue to reprocess later.
                logger.log("Status still in progress, check back later.");
                throw new RuntimeException("Job still running.");
            }
        }
    }
}