// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example

import aws.sdk.kotlin.services.sagemaker.SageMakerClient
import aws.sdk.kotlin.services.sagemaker.model.OutputParameter
import aws.sdk.kotlin.services.sagemaker.model.SendPipelineExecutionStepFailureRequest
import aws.sdk.kotlin.services.sagemaker.model.SendPipelineExecutionStepSuccessRequest
import aws.sdk.kotlin.services.sagemakergeospatial.SageMakerGeospatialClient
import aws.sdk.kotlin.services.sagemakergeospatial.model.ExportVectorEnrichmentJobOutputConfig
import aws.sdk.kotlin.services.sagemakergeospatial.model.ExportVectorEnrichmentJobRequest
import aws.sdk.kotlin.services.sagemakergeospatial.model.GetVectorEnrichmentJobRequest
import aws.sdk.kotlin.services.sagemakergeospatial.model.ReverseGeocodingConfig
import aws.sdk.kotlin.services.sagemakergeospatial.model.StartVectorEnrichmentJobRequest
import aws.sdk.kotlin.services.sagemakergeospatial.model.VectorEnrichmentJobConfig
import aws.sdk.kotlin.services.sagemakergeospatial.model.VectorEnrichmentJobDataSourceConfigInput
import aws.sdk.kotlin.services.sagemakergeospatial.model.VectorEnrichmentJobDocumentType
import aws.sdk.kotlin.services.sagemakergeospatial.model.VectorEnrichmentJobInputConfig
import aws.sdk.kotlin.services.sagemakergeospatial.model.VectorEnrichmentJobS3Data
import aws.sdk.kotlin.services.sagemakergeospatial.model.VectorEnrichmentJobStatus
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException

class SageMakerLambdaFunction : RequestHandler<HashMap<String, Any>, Map<String, String>> {

    override fun handleRequest(requestObject: HashMap<String, Any>, context: Context): Map<String, String> = runBlocking {
        val logger = context.logger

        val sageMakerClient = SageMakerClient {
            region = "us-west-2"
        }

        val geospatialClient = SageMakerGeospatialClient {
            region = "us-west-2"
        }

        logger.log("*** REQUEST: $requestObject")

        // The response dictionary.
        val responseDictionary: MutableMap<String, String> = hashMapOf()

        // Log out the values from the request. The request object is a HashMap.
        logger.log("*** vej_export_config: " + requestObject.get("vej_export_config"))
        logger.log("*** vej_name: " + requestObject.get("vej_name"))
        logger.log("*** vej_config: " + requestObject.get("vej_config"))
        logger.log("*** vej_input_config: " + requestObject.get("vej_input_config"))
        logger.log("*** role: " + requestObject.get("role"))

        // The Records array will be populated if this request came from the queue.
        logger.log("*** records: " + requestObject["Records"])

        if (requestObject["Records"] != null) {
            logger.log("Records found, this is a queue event. Processing the queue records.")
            val queueMessages = requestObject["Records"] as ArrayList<java.util.HashMap<String, String>>?
            for (message in queueMessages!!) {
                processMessage(geospatialClient, sageMakerClient, message["body"], context)
            }
        } else if (requestObject.get("vej_export_config") != null) {
            logger.log("*** Export configuration found. Start the Vector Enrichment Job (VEJ) export.")
            var jsonObject: JSONObject? = null
            val parser = JSONParser()

            jsonObject = try {
                parser.parse(requestObject["vej_export_config"] as String?) as JSONObject
            } catch (e: ParseException) {
                throw java.lang.RuntimeException("Problem parsing export config.")
            }

            val s3DataOb = jsonObject!!["S3Data"] as JSONObject
            val s3UriOb = s3DataOb["S3Uri"] as String
            println("**** NEW S3URI: $s3UriOb")

            val jobS3Data = VectorEnrichmentJobS3Data {
                s3Uri = s3UriOb
            }

            val jobOutputConfig = ExportVectorEnrichmentJobOutputConfig {
                s3Data = jobS3Data
            }

            val exportRequest = ExportVectorEnrichmentJobRequest {
                arn = requestObject["vej_arn"] as String?
                executionRoleArn = requestObject["role"] as String?
                outputConfig = jobOutputConfig
            }

            val exportVectorResponse = geospatialClient.exportVectorEnrichmentJob(exportRequest)
            val logger2 = context.logger
            logger2.log("Export response: " + exportVectorResponse.toString())

            responseDictionary["export_eoj_status"] = exportVectorResponse.exportStatus.toString()
            responseDictionary["vej_arn"] = exportVectorResponse.arn.toString()
        } else if (requestObject.get("vej_name") != null) {
            logger.log("*** NEW Vector Enrichment Job name found, starting the job.")

            var jsonObject: JSONObject? = null
            val parser = JSONParser()

            jsonObject = try {
                parser.parse(requestObject["vej_input_config"] as String?) as JSONObject
            } catch (e: ParseException) {
                throw java.lang.RuntimeException("Problem parsing input config.")
            }

            var dataSourceConfig1 = jsonObject!!["DataSourceConfig"] as JSONObject
            val s3DataObject = dataSourceConfig1["S3Data"] as JSONObject
            val s3UriObject = s3DataObject["S3Uri"] as String
            println("**** NEW S3URI: $s3UriObject")

            val s3DataOb = VectorEnrichmentJobS3Data {
                s3Uri = s3UriObject
            }

            val inputConfigVal = VectorEnrichmentJobInputConfig {
                documentType = VectorEnrichmentJobDocumentType.Csv
                dataSourceConfig = VectorEnrichmentJobDataSourceConfigInput.S3Data(s3DataOb)
            }

            val geocodingConfig = ReverseGeocodingConfig {
                xAttributeName = "Longitude"
                yAttributeName = "Latitude"
            }

            val jobRequest = StartVectorEnrichmentJobRequest {
                inputConfig = inputConfigVal
                executionRoleArn = requestObject["role"] as String?
                name = requestObject["vej_name"] as String?
                jobConfig = VectorEnrichmentJobConfig.ReverseGeocodingConfig(geocodingConfig)
            }

            logger.log("*** INVOKE geoSpatialClient.startVectorEnrichmentJob with client")
            SageMakerGeospatialClient { region = "us-west-2" }.use { geospatialObject ->
                val vecJobResponse = geospatialObject.startVectorEnrichmentJob(jobRequest)
                val vejArnValue = vecJobResponse.arn
                logger.log("vej_arn: $vejArnValue")
                val status = vecJobResponse.status
                logger.log("STATUS: $status")

                responseDictionary["statusCode"] = status.toString()
                responseDictionary["vej_arn"] = vejArnValue.toString()
            }
        }

        return@runBlocking responseDictionary
    }

    @Throws(RuntimeException::class)
    suspend fun processMessage(geoClient: SageMakerGeospatialClient, sageMakerClient: SageMakerClient, messageBody: String?, context: Context) {
        val gson = Gson()
        val logger = context.logger
        logger.log("Processing message with body:$messageBody")
        val queuePayload = gson.fromJson(messageBody, QueuePayload::class.java)
        val token = queuePayload.getToken()
        logger.log("Payload token $token")

        if (queuePayload.getArguments()!!.containsKey("vej_arn")) {
            // Use the job ARN and the token to get the job status.
            val jobArn = queuePayload.getArguments()!!["vej_arn"]
            logger.log("Token: $token, arn $jobArn")

            val jobInfoRequest = GetVectorEnrichmentJobRequest {
                arn = jobArn
            }

            val vectorResponse = geoClient.getVectorEnrichmentJob(jobInfoRequest)
            logger.log("Job info: " + vectorResponse.toString())

            if (vectorResponse.status?.equals(VectorEnrichmentJobStatus.Completed) == true) {
                logger.log("Status completed, resuming pipeline...")

                val out = OutputParameter {
                    name = "export_status"
                    value = java.lang.String.valueOf(vectorResponse.status)
                }

                val successRequest = SendPipelineExecutionStepSuccessRequest {
                    callbackToken = token
                    outputParameters = listOf(out)
                }
                sageMakerClient.sendPipelineExecutionStepSuccess(successRequest)
            } else if (vectorResponse.status?.equals(VectorEnrichmentJobStatus.Failed) == true) {
                logger.log("Status failed, stopping pipeline...")

                val failureRequest = SendPipelineExecutionStepFailureRequest {
                    callbackToken = token
                    failureReason = vectorResponse.errorDetails?.errorMessage
                }
                sageMakerClient.sendPipelineExecutionStepFailure(failureRequest)
            } else if (vectorResponse.status?.equals(VectorEnrichmentJobStatus.InProgress) == true) {
                // Put this message back in the queue to reprocess later.
                logger.log("Status still in progress, check back later.")
                throw RuntimeException("Job still running.")
            }
        }
    }
}
