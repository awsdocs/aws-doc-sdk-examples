// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.entity.scenario;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.fusesource.jansi.AnsiConsole;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.entityresolution.EntityResolutionAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.entityresolution.model.ConflictException;
import software.amazon.awssdk.services.entityresolution.model.CreateMatchingWorkflowRequest;
import software.amazon.awssdk.services.entityresolution.model.CreateMatchingWorkflowResponse;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingRequest;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;
import software.amazon.awssdk.services.entityresolution.model.DeleteMatchingWorkflowRequest;
import software.amazon.awssdk.services.entityresolution.model.DeleteMatchingWorkflowResponse;
import software.amazon.awssdk.services.entityresolution.model.DeleteSchemaMappingRequest;
import software.amazon.awssdk.services.entityresolution.model.DeleteSchemaMappingResponse;
import software.amazon.awssdk.services.entityresolution.model.GetMatchingJobRequest;
import software.amazon.awssdk.services.entityresolution.model.GetMatchingJobResponse;
import software.amazon.awssdk.services.entityresolution.model.GetSchemaMappingRequest;
import software.amazon.awssdk.services.entityresolution.model.GetSchemaMappingResponse;
import software.amazon.awssdk.services.entityresolution.model.InputSource;
import software.amazon.awssdk.services.entityresolution.model.JobMetrics;
import software.amazon.awssdk.services.entityresolution.model.ListSchemaMappingsRequest;
import software.amazon.awssdk.services.entityresolution.model.OutputAttribute;
import software.amazon.awssdk.services.entityresolution.model.OutputSource;
import software.amazon.awssdk.services.entityresolution.model.ResolutionTechniques;
import software.amazon.awssdk.services.entityresolution.model.ResolutionType;
import software.amazon.awssdk.services.entityresolution.model.ResourceNotFoundException;
import software.amazon.awssdk.services.entityresolution.model.SchemaAttributeType;
import software.amazon.awssdk.services.entityresolution.model.SchemaInputAttribute;
import software.amazon.awssdk.services.entityresolution.model.StartMatchingJobRequest;
import software.amazon.awssdk.services.entityresolution.model.TagResourceResponse;
import software.amazon.awssdk.services.entityresolution.model.ValidationException;
import software.amazon.awssdk.services.entityresolution.paginators.ListSchemaMappingsPublisher;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.entityresolution.model.TagResourceRequest;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.fusesource.jansi.Ansi.ansi;

// snippet-start:[entityres.java2_actions.main]
public class EntityResActions {

    private static final String PREFIX = "eroutput/";
    private static final Logger logger = LoggerFactory.getLogger(EntityResActions.class);

    private static EntityResolutionAsyncClient entityResolutionAsyncClient;

    private static S3AsyncClient s3AsyncClient;

    public static EntityResolutionAsyncClient getResolutionAsyncClient() {
        if (entityResolutionAsyncClient == null) {
            /*
            The `NettyNioAsyncHttpClient` class is part of the AWS SDK for Java, version 2,
            and it is designed to provide a high-performance, asynchronous HTTP client for interacting with AWS services.
             It uses the Netty framework to handle the underlying network communication and the Java NIO API to
             provide a non-blocking, event-driven approach to HTTP requests and responses.
             */

            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(50)  // Adjust as needed.
                .connectionTimeout(Duration.ofSeconds(60))  // Set the connection timeout.
                .readTimeout(Duration.ofSeconds(60))  // Set the read timeout.
                .writeTimeout(Duration.ofSeconds(60))  // Set the write timeout.
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))  // Set the overall API call timeout.
                .apiCallAttemptTimeout(Duration.ofSeconds(90))  // Set the individual call attempt timeout.
                .retryStrategy(RetryMode.STANDARD)
                .build();

            entityResolutionAsyncClient = EntityResolutionAsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return entityResolutionAsyncClient;
    }

    public static S3AsyncClient getS3AsyncClient() {
        if (s3AsyncClient == null) {
            /*
            The `NettyNioAsyncHttpClient` class is part of the AWS SDK for Java, version 2,
            and it is designed to provide a high-performance, asynchronous HTTP client for interacting with AWS services.
             It uses the Netty framework to handle the underlying network communication and the Java NIO API to
             provide a non-blocking, event-driven approach to HTTP requests and responses.
             */

            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(50)  // Adjust as needed.
                .connectionTimeout(Duration.ofSeconds(60))  // Set the connection timeout.
                .readTimeout(Duration.ofSeconds(60))  // Set the read timeout.
                .writeTimeout(Duration.ofSeconds(60))  // Set the write timeout.
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))  // Set the overall API call timeout.
                .apiCallAttemptTimeout(Duration.ofSeconds(90))  // Set the individual call attempt timeout.
                .retryStrategy(RetryMode.STANDARD)
                .build();

            s3AsyncClient = S3AsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return s3AsyncClient;
    }

    // snippet-start:[entityres.java2_delete_mappings.main]
    /**
     * Deletes the schema mapping asynchronously.
     *
     * @param schemaName the name of the schema to delete
     * @return a {@link CompletableFuture} that completes when the schema mapping is deleted successfully,
     * or throws a {@link RuntimeException} if the deletion fails
     */
    public CompletableFuture<DeleteSchemaMappingResponse> deleteSchemaMappingAsync(String schemaName) {
        DeleteSchemaMappingRequest request = DeleteSchemaMappingRequest.builder()
            .schemaName(schemaName)
            .build();

        return getResolutionAsyncClient().deleteSchemaMapping(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    // Successfully deleted the schema mapping, log the success message.
                    logger.info("Schema mapping '{}' deleted successfully.", schemaName);
                } else {
                    // Ensure exception is not null before accessing its cause.
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the schema mapping.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The schema mapping was not found to delete: " + schemaName, cause);
                    }

                    // Wrap other AWS exceptions in a CompletionException.
                    throw new CompletionException("Failed to delete schema mapping: " + schemaName, exception);
                }
            });
    }
    // snippet-end:[entityres.java2_delete_mappings.main]

    // snippet-start:[entityres.java2_list_mappings.main]
    /**
     * Lists the schema mappings associated with the current AWS account. This method uses an asynchronous paginator to
     * retrieve the schema mappings, and prints the name of each schema mapping to the console.
     */
    public void ListSchemaMappings() {
        ListSchemaMappingsRequest mappingsRequest = ListSchemaMappingsRequest.builder()
            .build();

        ListSchemaMappingsPublisher paginator = getResolutionAsyncClient().listSchemaMappingsPaginator(mappingsRequest);

        // Iterate through the pages of results
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            response.schemaList().forEach(schemaMapping ->
                logger.info("Schema Mapping Name: " + schemaMapping.schemaName())
            );
        });

        // Wait for the asynchronous operation to complete
        future.join();
    }
    // snippet-end:[entityres.java2_list_mappings.main]

    // snippet-start:[entityres.java2_delete_matching_workflow.main]
    /**
     * Asynchronously deletes a workflow with the specified name.
     *
     * @param workflowName the name of the workflow to be deleted
     * @return a {@link CompletableFuture} that completes when the workflow has been deleted
     * @throws RuntimeException if the deletion of the workflow fails
     */
    public CompletableFuture<DeleteMatchingWorkflowResponse> deleteMatchingWorkflowAsync(String workflowName) {
        DeleteMatchingWorkflowRequest request = DeleteMatchingWorkflowRequest.builder()
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().deleteMatchingWorkflow(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("{} was deleted", workflowName );
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while deleting the workflow.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The workflow to delete was not found.", cause);
                    }

                    // Wrap other AWS exceptions in a CompletionException.
                    throw new CompletionException("Failed to delete workflow: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_delete_matching_workflow.main]

    // snippet-start:[entityres.java2_create_schema.main]
    /**
     * Creates a schema mapping asynchronously.
     *
     * @param schemaName the name of the schema to create
     * @return a {@link CompletableFuture} that represents the asynchronous creation of the schema mapping
     */
    public CompletableFuture<CreateSchemaMappingResponse> createSchemaMappingAsync(String schemaName) {
        List<SchemaInputAttribute> schemaAttributes = null;
        if (schemaName.startsWith("json")) {
            schemaAttributes = List.of(
                SchemaInputAttribute.builder().matchKey("id").fieldName("id").type(SchemaAttributeType.UNIQUE_ID).build(),
                SchemaInputAttribute.builder().matchKey("name").fieldName("name").type(SchemaAttributeType.NAME).build(),
                SchemaInputAttribute.builder().matchKey("email").fieldName("email").type(SchemaAttributeType.EMAIL_ADDRESS).build()
            );
        } else {
            schemaAttributes = List.of(
                SchemaInputAttribute.builder().matchKey("id").fieldName("id").type(SchemaAttributeType.UNIQUE_ID).build(),
                SchemaInputAttribute.builder().matchKey("name").fieldName("name").type(SchemaAttributeType.NAME).build(),
                SchemaInputAttribute.builder().matchKey("email").fieldName("email").type(SchemaAttributeType.EMAIL_ADDRESS).build(),
                SchemaInputAttribute.builder().fieldName("phone").type(SchemaAttributeType.PROVIDER_ID).subType("STRING").build()
            );
        }

        CreateSchemaMappingRequest request = CreateSchemaMappingRequest.builder()
            .schemaName(schemaName)
            .mappedInputFields(schemaAttributes)
            .build();

        return getResolutionAsyncClient().createSchemaMapping(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("[{}] schema mapping Created Successfully!", schemaName);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while creating the schema mapping.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ConflictException) {
                        throw new CompletionException("A conflicting schema mapping already exists. Resolve conflicts before proceeding.", cause);
                    }

                    // Wrap other AWS exceptions in a CompletionException.
                    throw new CompletionException("Failed to create schema mapping: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_create_schema.main]

    // snippet-start:[entityres.java2_get_schema_mapping.main]
    /**
     * Retrieves the schema mapping asynchronously.
     *
     * @param schemaName the name of the schema to retrieve the mapping for
     * @return a {@link CompletableFuture} that completes with the {@link GetSchemaMappingResponse} when the operation
     * is complete
     * @throws RuntimeException if the schema mapping retrieval fails
     */
    public CompletableFuture<GetSchemaMappingResponse> getSchemaMappingAsync(String schemaName) {
        GetSchemaMappingRequest mappingRequest = GetSchemaMappingRequest.builder()
            .schemaName(schemaName)
            .build();

        return getResolutionAsyncClient().getSchemaMapping(mappingRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    response.mappedInputFields().forEach(attribute ->
                        logger.info("Attribute Name: " + attribute.fieldName() +
                            ", Attribute Type: " + attribute.type().toString()));
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while getting schema mapping.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested schema mapping was not found.", cause);
                    }

                    // Wrap other exceptions in a CompletionException with the message.
                    throw new CompletionException("Failed to get schema mapping: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_get_schema_mapping.main]

    // snippet-start:[entityres.java2_get_job.main]
    /**
     * Asynchronously retrieves a matching job based on the provided job ID and workflow name.
     *
     * @param jobId        the ID of the job to retrieve
     * @param workflowName the name of the workflow associated with the job
     * @return a {@link CompletableFuture} that completes when the job information is available or an exception occurs
     */
    public CompletableFuture<GetMatchingJobResponse> getMatchingJobAsync(String jobId, String workflowName) {
        GetMatchingJobRequest request = GetMatchingJobRequest.builder()
            .jobId(jobId)
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().getMatchingJob(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    // Successfully fetched the matching job details, log the job status.
                    logger.info("Job status: " + response.status());
                    logger.info("Job details: " + response.toString());
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while fetching the matching job.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested job could not be found.", cause);
                    }

                    // Wrap other exceptions in a CompletionException with the message.
                    throw new CompletionException("Error fetching matching job: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_get_job.main]

    // snippet-start:[entityres.java2_start_job.main]

    /**
     * Starts a matching job asynchronously for the specified workflow name.
     *
     * @param workflowName the name of the workflow for which to start the matching job
     * @return a {@link CompletableFuture} that completes with the job ID of the started matching job, or an empty
     * string if the operation fails
     */
    public CompletableFuture<String> startMatchingJobAsync(String workflowName) {
        StartMatchingJobRequest jobRequest = StartMatchingJobRequest.builder()
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().startMatchingJob(jobRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    String jobId = response.jobId();
                    logger.info("Job ID: " + jobId);
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while starting the job.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ConflictException) {
                        throw new CompletionException("The job is already running. Resolve conflicts before starting a new job.", cause);
                    }

                    // Wrap other AWS exceptions in a CompletionException.
                    throw new CompletionException("Failed to start the job: " + exception.getMessage(), exception);
                }
            })
            .thenApply(response -> response != null ? response.jobId() : "");
    }
    // snippet-end:[entityres.java2_start_job.main]

    // snippet-start:[entityres.java2_check_matching_workflow.main]
    /**
     * Checks the status of a workflow asynchronously.
     *
     * @param jobId        the ID of the job to check
     * @param workflowName the name of the workflow to check
     * @return a CompletableFuture that resolves to a boolean value indicating whether the workflow has completed
     * successfully
     */
    public CompletableFuture<GetMatchingJobResponse> checkWorkflowStatusCompleteAsync(String jobId, String workflowName) {
        GetMatchingJobRequest request = GetMatchingJobRequest.builder()
            .jobId(jobId)
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().getMatchingJob(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    // Process the response and log the job status.
                    logger.info("Job status: " + response.status());
                } else {
                    // Ensure exception is not null before accessing its cause.
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while checking job status.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The requested resource was not found while checking the job status.", cause);
                    }

                    // Wrap other AWS exceptions in a CompletionException.
                    throw new CompletionException("Failed to check job status: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_check_matching_workflow.main]

    // snippet-start:[entityres.java2_create_matching_workflow.main]
    /**
     * Creates an asynchronous CompletableFuture to manage the creation of a matching workflow.
     *
     * @param roleARN                 the AWS IAM role ARN to be used for the workflow execution
     * @param workflowName            the name of the workflow to be created
     * @param outputBucket            the S3 bucket path where the workflow output will be stored
     * @param jsonGlueTableArn        the ARN of the Glue Data Catalog table to be used as the input source
     * @param jsonErSchemaMappingName the name of the schema to be used for the input source
     * @return a CompletableFuture that, when completed, will return the ARN of the created workflow
     */
    public CompletableFuture<String> createMatchingWorkflowAsync(
        String roleARN
        , String workflowName
        , String outputBucket
        , String jsonGlueTableArn
        , String jsonErSchemaMappingName
        , String csvGlueTableArn
        , String csvErSchemaMappingName) {

        InputSource jsonInputSource = InputSource.builder()
            .inputSourceARN(jsonGlueTableArn)
            .schemaName(jsonErSchemaMappingName)
            .applyNormalization(false)
            .build();

        InputSource csvInputSource = InputSource.builder()
            .inputSourceARN(csvGlueTableArn)
            .schemaName(csvErSchemaMappingName)
            .applyNormalization(false)
            .build();

        OutputAttribute idOutputAttribute = OutputAttribute.builder()
            .name("id")
            .build();

        OutputAttribute nameOutputAttribute = OutputAttribute.builder()
            .name("name")
            .build();

        OutputAttribute emailOutputAttribute = OutputAttribute.builder()
            .name("email")
            .build();

        OutputAttribute phoneOutputAttribute = OutputAttribute.builder()
            .name("phone")
            .build();

        OutputSource outputSource = OutputSource.builder()
            .outputS3Path("s3://" + outputBucket + "/eroutput")
            .output(idOutputAttribute, nameOutputAttribute, emailOutputAttribute, phoneOutputAttribute)
            .applyNormalization(false)
            .build();

        ResolutionTechniques resolutionType = ResolutionTechniques.builder()
            .resolutionType(ResolutionType.ML_MATCHING)
            .build();

        CreateMatchingWorkflowRequest workflowRequest = CreateMatchingWorkflowRequest.builder()
            .roleArn(roleARN)
            .description("Created by using the AWS SDK for Java")
            .workflowName(workflowName)
            .inputSourceConfig(List.of(jsonInputSource, csvInputSource))
            .outputSourceConfig(List.of(outputSource))
            .resolutionTechniques(resolutionType)
            .build();

        return getResolutionAsyncClient().createMatchingWorkflow(workflowRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Workflow created successfully.");
                } else {
                    Throwable cause = exception.getCause();
                    if (cause instanceof ValidationException) {
                        throw new CompletionException("Invalid request: Please check input parameters.", cause);
                    }

                    if (cause instanceof ConflictException) {
                        throw new CompletionException("A conflicting workflow already exists. Resolve conflicts before proceeding.", cause);
                    }
                    throw new CompletionException("Failed to create workflow: " + exception.getMessage(), exception);
                }
            })
            .thenApply(CreateMatchingWorkflowResponse::workflowArn);
    }
    // snippet-end:[entityres.java2_create_matching_workflow.main]

    // snippet-start:[entityres.java2_tag_resource.main]
    /**
     * Tags the specified schema mapping ARN.
     *
     * @param schemaMappingARN the ARN of the schema mapping to tag
     */
    public CompletableFuture<TagResourceResponse> tagEntityResource(String schemaMappingARN) {
        Map<String, String> tags = new HashMap<>();
        tags.put("tag1", "tag1Value");
        tags.put("tag2", "tag2Value");

        TagResourceRequest request = TagResourceRequest.builder()
            .resourceArn(schemaMappingARN)
            .tags(tags)
            .build();

        return getResolutionAsyncClient().tagResource(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    // Successfully tagged the resource, log the success message.
                    logger.info("Successfully tagged the resource.");
                } else {
                    if (exception == null) {
                        throw new CompletionException("An unknown error occurred while tagging the resource.", null);
                    }

                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("The resource to tag was not found.", cause);
                    }
                    throw new CompletionException("Failed to tag the resource: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_tag_resource.main]

    // snippet-start:[entityres.java2_job_info.main]
    public CompletableFuture<JobMetrics> getJobInfo(String workflowName, String jobId) {
        return getResolutionAsyncClient().getMatchingJob(b -> b
                .workflowName(workflowName)
                .jobId(jobId))
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Job metrics fetched successfully for jobId: " + jobId);
                } else {
                    Throwable cause = exception.getCause();
                    if (cause instanceof ResourceNotFoundException) {
                        throw new CompletionException("Invalid request: Job id was not found.", cause);
                    }
                    throw new CompletionException("Failed to fetch job info: " + exception.getMessage(), exception);
                }
            })
            .thenApply(response -> response.metrics()); // Extract job metrics
    }
    // snippet-end:[entityres.java2_job_info.main]

    /**
     * Uploads data to an Amazon S3 bucket asynchronously.
     *
     * @param bucketName the name of the S3 bucket to upload the data to
     * @param jsonData   the JSON data to be uploaded
     * @param csvData    the CSV data to be uploaded
     * @return a {@link CompletableFuture} representing both asynchronous operation of uploading the data
     * @throws RuntimeException if an error occurs during the file upload
     */

    public void uploadInputData(String bucketName, String jsonData, String csvData) {
        // Upload JSON data.
        String jsonKey = "jsonData/data.json";
        PutObjectRequest jsonUploadRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(jsonKey)
            .contentType("application/json")
            .build();

        CompletableFuture<PutObjectResponse> jsonUploadResponse = getS3AsyncClient().putObject(jsonUploadRequest, AsyncRequestBody.fromString(jsonData));

        // Upload CSV data.
        String csvKey = "csvData/data.csv";
        PutObjectRequest csvUploadRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(csvKey)
            .contentType("text/csv")
            .build();
        CompletableFuture<PutObjectResponse> csvUploadResponse = getS3AsyncClient().putObject(csvUploadRequest, AsyncRequestBody.fromString(csvData));

        CompletableFuture.allOf(jsonUploadResponse, csvUploadResponse)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    // Wrap an AWS exception.
                    throw new CompletionException("Failed to upload files", ex);
                }
            }).join();

    }

    /**
     * Finds the latest file in the S3 bucket that starts with "run-" in any depth of subfolders
     */
    private CompletableFuture<String> findLatestMatchingFile(String bucketName) {
        ListObjectsV2Request request = ListObjectsV2Request.builder()
            .bucket(bucketName)
            .prefix(PREFIX) // Searches within the given folder
            .build();

        return getS3AsyncClient().listObjectsV2(request)
            .thenApply(response -> response.contents().stream()
                .map(S3Object::key)
                .filter(key -> key.matches(".*?/run-[0-9a-zA-Z\\-]+")) // Matches files like run-XXXXX in any subfolder
                .max(String::compareTo) // Gets the latest file
                .orElse(null))
            .whenComplete((result, exception) -> {
                if (exception == null) {
                    if (result != null) {
                        logger.info("Latest matching file found: " + result);
                    } else {
                        logger.info("No matching files found.");
                    }
                } else {
                    throw new CompletionException("Failed to find latest matching file: " + exception.getMessage(), exception);
                }
            });
    }

    /**
     * Prints the data located in the file in the S3 bucket that starts with "run-" in any depth of subfolders
     */
    public void printData(String bucketName) {
        try {
            // Find the latest file with "run-" prefix in any depth of subfolders.
            String s3Key = findLatestMatchingFile(bucketName).join();
            if (s3Key == null) {
                logger.error("No matching files found in S3.");
                return;
            }

            logger.info("Downloading file: " + s3Key);

            // Read CSV file as String.
            String csvContent = readCSVFromS3Async(bucketName, s3Key).join();
            if (csvContent.isEmpty()) {
                logger.error("File is empty.");
                return;
            }

            // Process CSV content.
            List<String[]> records = parseCSV(csvContent);
            printTable(records);

        } catch (RuntimeException | IOException | CsvException e) {
            logger.error("Error processing CSV file from S3: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Reads a CSV file from S3 and returns it as a String.
     */
    private static CompletableFuture<String> readCSVFromS3Async(String bucketName, String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3Key)
            .build();

        // Initiating the asynchronous request to get the file as bytes
        return getS3AsyncClient().getObject(getObjectRequest, AsyncResponseTransformer.toBytes())
            .thenApply(responseBytes -> responseBytes.asUtf8String()) // Convert bytes to UTF-8 string
            .whenComplete((result, exception) -> {
                if (exception != null) {
                    throw new CompletionException("Failed to read CSV from S3: " + exception.getMessage(), exception);
                } else {
                    logger.info("Successfully fetched CSV file content from S3.");
                }
            });
    }

    /**
     * Parses CSV content from a String into a list of records.
     */
    private static List<String[]> parseCSV(String csvContent) throws IOException, CsvException {
        try (CSVReader csvReader = new CSVReader(new StringReader(csvContent))) {
            return csvReader.readAll();
        }
    }

    /**
     * Prints the given CSV data in a formatted table
     */
    private static void printTable(List<String[]> records) {
        if (records.isEmpty()) {
            System.out.println("No records found.");
            return;
        }

        String[] headers = records.get(0);
        List<String[]> rows = records.subList(1, records.size());

        // Determine column widths dynamically based on longest content
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            final int columnIndex = i;
            int maxWidth = Math.max(headers[i].length(), rows.stream()
                .map(row -> row.length > columnIndex ? row[columnIndex].length() : 0)
                .max(Integer::compareTo)
                .orElse(0));
            columnWidths[i] = Math.min(maxWidth, 25); // Limit max width for better readability
        }

        // Enable ANSI Console for colored output
        AnsiConsole.systemInstall();

        // Print table header
        System.out.println(ansi().fgYellow().a("=== CSV Data from S3 ===").reset());
        printRow(headers, columnWidths, true);

        // Print rows
        rows.forEach(row -> printRow(row, columnWidths, false));

        // Restore console to normal
        AnsiConsole.systemUninstall();
    }

    private static void printRow(String[] row, int[] columnWidths, boolean isHeader) {
        String border = IntStream.range(0, columnWidths.length)
            .mapToObj(i -> "-".repeat(columnWidths[i] + 2))
            .collect(Collectors.joining("+", "+", "+"));

        if (isHeader) {
            System.out.println(border);
        }

        System.out.print("|");
        for (int i = 0; i < columnWidths.length; i++) {
            String cell = (i < row.length && row[i] != null) ? row[i] : "";
            System.out.printf(" %-" + columnWidths[i] + "s |", isHeader ? ansi().fgBrightBlue().a(cell).reset() : cell);
        }
        System.out.println();

        if (isHeader) {
            System.out.println(border);
        }
    }
}
// snippet-end:[entityres.java2_actions.main]