// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.entity.scenario;

import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.entityresolution.EntityResolutionAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.entityresolution.model.CreateMatchingWorkflowRequest;
import software.amazon.awssdk.services.entityresolution.model.CreateMatchingWorkflowResponse;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingRequest;
import software.amazon.awssdk.services.entityresolution.model.CreateSchemaMappingResponse;
import software.amazon.awssdk.services.entityresolution.model.DeleteMatchingWorkflowRequest;
import software.amazon.awssdk.services.entityresolution.model.GetMatchingJobRequest;
import software.amazon.awssdk.services.entityresolution.model.GetSchemaMappingRequest;
import software.amazon.awssdk.services.entityresolution.model.GetSchemaMappingResponse;
import software.amazon.awssdk.services.entityresolution.model.InputSource;
import software.amazon.awssdk.services.entityresolution.model.ListSchemaMappingsRequest;
import software.amazon.awssdk.services.entityresolution.model.OutputAttribute;
import software.amazon.awssdk.services.entityresolution.model.OutputSource;
import software.amazon.awssdk.services.entityresolution.model.ResolutionTechniques;
import software.amazon.awssdk.services.entityresolution.model.ResolutionType;
import software.amazon.awssdk.services.entityresolution.model.SchemaInputAttribute;
import software.amazon.awssdk.services.entityresolution.model.StartMatchingJobRequest;
import software.amazon.awssdk.services.entityresolution.paginators.ListSchemaMappingsPublisher;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.entityresolution.model.TagResourceRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EntityResActions {
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
                .region(Region.US_EAST_1)
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
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return s3AsyncClient;
    }

    // snippet-start:[entityres.java2_list_mappings.main]
    /**
     * Lists the schema mappings associated with the current AWS account.
     * This method uses an asynchronous paginator to retrieve the schema mappings,
     * and prints the name of each schema mapping to the console.
     */
    public void ListSchemaMappings() {
        ListSchemaMappingsRequest mappingsRequest = ListSchemaMappingsRequest.builder()
            .build();

        ListSchemaMappingsPublisher paginator = getResolutionAsyncClient().listSchemaMappingsPaginator(mappingsRequest);

       // Iterate through the pages of results
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            response.schemaList().forEach(schemaMapping ->
                logger.info("Schema Mapping Name: " +schemaMapping.schemaName())
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
    public CompletableFuture<Void> deleteMatchingWorkflowAsync(String workflowName) {
        DeleteMatchingWorkflowRequest request = DeleteMatchingWorkflowRequest.builder()
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().deleteMatchingWorkflow(request)
            .thenAccept(response -> {
                // No response object, just log success
            })
            .exceptionally(exception -> {
                throw new RuntimeException("Failed to delete workflow: " + exception.getMessage(), exception);
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
        List<SchemaInputAttribute> schemaAttributes = List.of(
            SchemaInputAttribute.builder().matchKey("id").fieldName("id").type("UNIQUE_ID").build(),
            SchemaInputAttribute.builder().matchKey("name").fieldName("name").type("STRING").build(),
            SchemaInputAttribute.builder().matchKey("email").fieldName("email").type("STRING").build()
        );

        CreateSchemaMappingRequest request = CreateSchemaMappingRequest.builder()
            .schemaName(schemaName)
            .mappedInputFields(schemaAttributes)
            .build();

        return getResolutionAsyncClient().createSchemaMapping(request)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Schema Mapping Created Successfully!");
                } else {
                    throw new RuntimeException("Failed to create schema mapping: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_create_schema.main]

    // snippet-start:[entityres.java2_get_schema_mapping.main]
    /**
     * Retrieves the schema mapping asynchronously.
     *
     * @param schemaName the name of the schema to retrieve the mapping for
     * @return a {@link CompletableFuture} that completes with the {@link GetSchemaMappingResponse} when the operation is complete
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
                    throw new RuntimeException("Failed to get schema mapping: " + exception.getMessage(), exception);
                }
            });
    }
    // snippet-end:[entityres.java2_get_schema_mapping.main]

    // snippet-start:[entityres.java2_get_job.main]
    /**
     * Asynchronously retrieves a matching job based on the provided job ID and workflow name.
     *
     * @param jobId         the ID of the job to retrieve
     * @param workflowName  the name of the workflow associated with the job
     * @return a {@link CompletableFuture} that completes when the job information is available or an exception occurs
     */
    public CompletableFuture<Void> getMatchingJobAsync(String jobId, String workflowName) {
        GetMatchingJobRequest request = GetMatchingJobRequest.builder()
            .jobId(jobId)
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().getMatchingJob(request)
            .thenAccept(response -> {
                logger.info("Job status: " + response.status());
                logger.info("Job details: " + response.toString());
            })
            .exceptionally(ex -> {
                throw new RuntimeException("Error fetching matching job: " + ex.getMessage(), ex);
            });
    }
    // snippet-end:[entityres.java2_get_job.main]

    // snippet-start:[entityres.java2_start_job.main]
    /**
     * Starts a matching job asynchronously for the specified workflow name.
     *
     * @param workflowName the name of the workflow for which to start the matching job
     * @return a {@link CompletableFuture} that completes with the job ID of the started matching job, or an empty string if the operation fails
     */
    public CompletableFuture<String> startMatchingJobAsync(String workflowName) {
        StartMatchingJobRequest jobRequest = StartMatchingJobRequest.builder()
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().startMatchingJob(jobRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    // Get the job ID from the response
                    String jobId = response.jobId();
                    logger.info("Job ID: " + jobId);
                } else {
                    // Handle the exception if the response is null
                    throw new RuntimeException("Failed to start matching job: " + exception.getMessage(), exception);
                }
            })
            .thenApply(response -> response != null ? response.jobId() : "");
    }
    // snippet-end:[entityres.java2_start_job.main]

    // snippet-start:[entityres.java2_check_matching_workflow.main]
    /**
     * Checks the status of a workflow asynchronously.
     *
     * @param jobId         the ID of the job to check
     * @param workflowName  the name of the workflow to check
     * @return a CompletableFuture that resolves to a boolean value indicating whether the workflow has completed successfully
     */
    public CompletableFuture<Boolean> checkWorkflowStatusCompleteAsync(String jobId, String workflowName) {
        GetMatchingJobRequest request = GetMatchingJobRequest.builder()
            .jobId(jobId)
            .workflowName(workflowName)
            .build();

        return getResolutionAsyncClient().getMatchingJob(request)
            .thenApply(response -> {
                logger.info("\nJob status: " + response.status());
                return "SUCCEEDED".equalsIgnoreCase(String.valueOf(response.status()));
            })
            .exceptionally(exception -> {
                logger.info("Error checking workflow status: " + exception.getMessage());
                return false;
            });
    }
    // snippet-end:[entityres.java2_check_matching_workflow.main]

    // snippet-start:[entityres.java2_create_matching_workflow.main]
    /**
     * Creates an asynchronous CompletableFuture to manage the creation of a matching workflow.
     *
     * @param roleARN             the AWS IAM role ARN to be used for the workflow execution
     * @param workflowName        the name of the workflow to be created
     * @param outputBucket        the S3 bucket path where the workflow output will be stored
     * @param inputGlueTableArn   the ARN of the Glue Data Catalog table to be used as the input source
     * @param schemaName          the name of the schema to be used for the input source
     * @return a CompletableFuture that, when completed, will return the ARN of the created workflow
     */
    public CompletableFuture<String> createMatchingWorkflowAsync(String roleARN, String workflowName, String outputBucket, String inputGlueTableArn, String schemaName) {
        InputSource inputSource = InputSource.builder()
            .inputSourceARN(inputGlueTableArn)
            .schemaName(schemaName)
            .build();

        OutputAttribute outputAttribute = OutputAttribute.builder()
            .name("id")
            .build();

        OutputSource outputSource = OutputSource.builder()
            .outputS3Path(outputBucket)
            .output(outputAttribute)
            .build();

        ResolutionTechniques type = ResolutionTechniques.builder()
            .resolutionType(ResolutionType.ML_MATCHING)
            .build();

        CreateMatchingWorkflowRequest workflowRequest = CreateMatchingWorkflowRequest.builder()
            .roleArn(roleARN)
            .description("Created by using the AWS SDK for Java")
            .workflowName(workflowName)
            .inputSourceConfig(List.of(inputSource))
            .outputSourceConfig(List.of(outputSource))
            .resolutionTechniques(type)
            .build();

        return getResolutionAsyncClient().createMatchingWorkflow(workflowRequest)
            .whenComplete((response, exception) -> {
                if (response != null) {
                    logger.info("Workflow created successfully.");
                } else {
                    throw new RuntimeException("Failed to create workflow: " + exception.getMessage(), exception);
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
    public CompletableFuture<Void> tagEntityResource(String schemaMappingARN) {
        Map<String, String> tags = new HashMap<>();
        tags.put("tag1", "tag1Value");
        tags.put("tag2", "tag2Value");

        TagResourceRequest request = TagResourceRequest.builder()
            .resourceArn(schemaMappingARN)
            .tags(tags)
            .build();

        return getResolutionAsyncClient().tagResource(request)
            .thenAccept(response -> logger.info("Successfully tagged the resource."))
            .exceptionally(exception -> {
                throw new RuntimeException("Failed to tag the resource: " + exception.getMessage(), exception);
            });
    }

    // snippet-end:[entityres.java2_tag_resource.main]

    /**
     * Uploads a local file to an Amazon S3 bucket asynchronously.
     *
     * @param bucketName the name of the S3 bucket to upload the file to
     * @param json the JSON data to be uploaded
     * @return a {@link CompletableFuture} representing the asynchronous operation of uploading the file
     * @throws RuntimeException if an error occurs during the file upload
     */
    public CompletableFuture<PutObjectResponse> uploadLocalFileAsync(String bucketName, String json) {

        String key = "data/data.json"; // Corrected: No leading "/"
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .contentType("application/json")
            .build();

        CompletableFuture<PutObjectResponse> response = getS3AsyncClient().putObject(objectRequest, AsyncRequestBody.fromString(json));
        return response.whenComplete((resp, ex) -> {
            if (ex != null) {
                throw new RuntimeException("Failed to upload file", ex);
            }
        });
    }

    /**
     * Checks if a specific object exists in an Amazon S3 bucket.
     *
     * @param bucketName the name of the S3 bucket to check
     * @return true if the object exists, false otherwise
     */
    public boolean doesObjectExist(String bucketName) {
        try {
            String key = "data/data.json";
            getS3AsyncClient().headObject(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
            return true; // File exists

        } catch (S3Exception e) {
            return false;
        }
    }
}
