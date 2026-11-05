// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.athena.basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.athena.model.CreateNamedQueryResponse;
import software.amazon.awssdk.services.athena.model.GetQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.GetWorkGroupResponse;
import software.amazon.awssdk.services.athena.model.Row;
import software.amazon.awssdk.services.athena.model.StartQueryExecutionResponse;
import software.amazon.awssdk.services.athena.model.WorkGroup;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.CreateStackRequest;
import software.amazon.awssdk.services.cloudformation.model.DeleteStackRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksRequest;
import software.amazon.awssdk.services.cloudformation.model.DescribeStacksResponse;
import software.amazon.awssdk.services.cloudformation.model.Output;
import software.amazon.awssdk.services.cloudformation.model.Stack;
import software.amazon.awssdk.services.cloudformation.model.StackStatus;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionException;

/**
 * Amazon Athena Basics Scenario - demonstrates the core Athena operations.
 */
// snippet-start:[athena.java2.scenario.main]
public class AthenaScenario {
    private static final Logger logger = LoggerFactory.getLogger(AthenaScenario.class);

    public static void main(String[] args) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String stackName = "athena-basics-" + uniqueId;
        String bucketName = "athena-results-" + uniqueId;
        String workGroupName = "athena-basics-" + uniqueId;
        String namedQueryId = null;

        CloudFormationAsyncClient cfnClient = CloudFormationAsyncClient.builder().build();
        S3AsyncClient s3Client = S3AsyncClient.builder().build();
        AthenaActions actions = new AthenaActions();

        try {
            // Setup: Create S3 bucket via CloudFormation
            logger.info("=== Amazon Athena Basics Scenario ===");
            logger.info("Setting up resources...");

            String template = "{\n" +
                "  \"AWSTemplateFormatVersion\": \"2010-09-09\",\n" +
                "  \"Resources\": {\n" +
                "    \"AthenaBucket\": {\n" +
                "      \"Type\": \"AWS::S3::Bucket\",\n" +
                "      \"Properties\": {\n" +
                "        \"BucketName\": \"" + bucketName + "\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"Outputs\": {\n" +
                "    \"BucketName\": {\n" +
                "      \"Value\": {\"Ref\": \"AthenaBucket\"}\n" +
                "    }\n" +
                "  }\n" +
                "}";

            cfnClient.createStack(CreateStackRequest.builder()
                .stackName(stackName)
                .templateBody(template)
                .build()).join();

            logger.info("Creating CloudFormation stack: {}", stackName);
            waitForStack(cfnClient, stackName, StackStatus.CREATE_COMPLETE);
            logger.info("Stack created successfully.");

            String s3OutputLocation = "s3://" + bucketName + "/results/";

            // Step 1: Create a workgroup
            logger.info("\nStep 1: Creating workgroup '{}'...", workGroupName);
            actions.createWorkGroupAsync(workGroupName, "Athena Basics demo workgroup", s3OutputLocation).join();
            logger.info("Workgroup '{}' created.", workGroupName);

            // Step 2: Get workgroup details
            logger.info("\nStep 2: Getting workgroup details...");
            GetWorkGroupResponse getWgResponse = actions.getWorkGroupAsync(workGroupName).join();
            WorkGroup wg = getWgResponse.workGroup();
            logger.info("  Name: {}", wg.name());
            logger.info("  State: {}", wg.state());
            logger.info("  Description: {}", wg.description());
            logger.info("  Output location: {}", wg.configuration().resultConfiguration().outputLocation());

            // Step 3: Create a named query
            logger.info("\nStep 3: Creating a named query...");
            CreateNamedQueryResponse namedQueryResp = actions.createNamedQueryAsync(
                "sample-select-query",
                "A sample query that selects all records from a table",
                "default",
                "SELECT 1 AS test_column",
                workGroupName
            ).join();
            namedQueryId = namedQueryResp.namedQueryId();
            logger.info("Named query created with ID: {}", namedQueryId);

            // Step 4: Create a prepared statement
            logger.info("\nStep 4: Creating a prepared statement...");
            actions.createPreparedStatementAsync(
                "sample-prepared-stmt",
                "SELECT ? AS greeting",
                workGroupName
            ).join();
            logger.info("Prepared statement created.");

            // Step 5: Start a query execution
            logger.info("\nStep 5: Starting query execution...");
            StartQueryExecutionResponse startResp = actions.startQueryExecutionAsync(
                "SELECT 1 AS id, 'Hello Athena' AS message",
                workGroupName
            ).join();
            String queryExecutionId = startResp.queryExecutionId();
            logger.info("Query execution started with ID: {}", queryExecutionId);

            // Step 6: Wait for query to complete
            logger.info("\nStep 6: Waiting for query to complete...");
            GetQueryExecutionResponse execResponse = actions.waitForQueryExecutionAsync(queryExecutionId).join();
            logger.info("Query completed with state: {}",
                execResponse.queryExecution().status().state());
            if (execResponse.queryExecution().statistics() != null) {
                logger.info("  Data scanned: {} bytes",
                    execResponse.queryExecution().statistics().dataScannedInBytes());
                logger.info("  Execution time: {} ms",
                    execResponse.queryExecution().statistics().engineExecutionTimeInMillis());
            }

            // Step 7: Get query results
            logger.info("\nStep 7: Getting query results...");
            List<Row> rows = actions.getQueryResultsAsync(queryExecutionId).join();
            if (!rows.isEmpty()) {
                // First row is the header
                Row header = rows.get(0);
                logger.info("Columns: {}", header.data().stream()
                    .map(d -> d.varCharValue())
                    .reduce((a, b) -> a + ", " + b).orElse(""));
                for (int i = 1; i < rows.size(); i++) {
                    Row row = rows.get(i);
                    logger.info("Row {}: {}", i, row.data().stream()
                        .map(d -> d.varCharValue())
                        .reduce((a, b) -> a + ", " + b).orElse(""));
                }
            }
            logger.info("Retrieved {} rows (including header).", rows.size());

            // Step 8: List query executions
            logger.info("\nStep 8: Listing query executions...");
            List<String> executionIds = actions.listQueryExecutionsAsync(workGroupName).join();
            logger.info("Found {} query execution(s).", executionIds.size());
            int displayCount = Math.min(5, executionIds.size());
            for (int i = 0; i < displayCount; i++) {
                logger.info("  Execution ID: {}", executionIds.get(i));
            }
            if (executionIds.size() > 5) {
                logger.info("  ... and {} more.", executionIds.size() - 5);
            }

        } catch (CompletionException e) {
            logger.error("Scenario failed: {}", e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        } catch (Exception e) {
            logger.error("Scenario failed: {}", e.getMessage());
        } finally {
            // Cleanup
            logger.info("\n=== Cleaning up resources ===");

            // Delete named query
            if (namedQueryId != null) {
                try {
                    actions.deleteNamedQueryAsync(namedQueryId).join();
                    logger.info("Deleted named query: {}", namedQueryId);
                } catch (Exception e) {
                    logger.warn("Could not delete named query: {}", e.getMessage());
                }
            }

            // Delete workgroup
            try {
                actions.deleteWorkGroupAsync(workGroupName).join();
                logger.info("Deleted workgroup: {}", workGroupName);
            } catch (Exception e) {
                logger.warn("Could not delete workgroup: {}", e.getMessage());
            }

            // Empty and delete the S3 bucket, then delete the stack
            try {
                emptyBucket(s3Client, bucketName);
                cfnClient.deleteStack(DeleteStackRequest.builder()
                    .stackName(stackName)
                    .build()).join();
                logger.info("Deleting CloudFormation stack: {}", stackName);
                waitForStackDeletion(cfnClient, stackName);
                logger.info("Stack deleted.");
            } catch (Exception e) {
                logger.warn("Could not delete stack: {}", e.getMessage());
            }

            cfnClient.close();
            s3Client.close();

            logger.info("\nAmazon Athena Basics scenario complete!");
            logger.info("Successfully demonstrated:");
            logger.info(" - Creating and inspecting a workgroup with S3 output");
            logger.info(" - Saving a named query for reuse");
            logger.info(" - Creating a prepared statement");
            logger.info(" - Running a SQL query and waiting for completion");
            logger.info(" - Retrieving paginated query results");
            logger.info(" - Listing paginated query execution history");
            logger.info(" - Cleaning up all resources");
        }
    }

    private static void waitForStack(CloudFormationAsyncClient cfnClient, String stackName,
                                     StackStatus targetStatus) {
        int maxAttempts = 60;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                DescribeStacksResponse resp = cfnClient.describeStacks(DescribeStacksRequest.builder()
                    .stackName(stackName)
                    .build()).join();
                if (!resp.stacks().isEmpty()) {
                    StackStatus status = resp.stacks().get(0).stackStatus();
                    if (status == targetStatus) {
                        return;
                    }
                    if (status.toString().endsWith("FAILED") || status.toString().endsWith("ROLLBACK_COMPLETE")) {
                        throw new RuntimeException("Stack reached status: " + status);
                    }
                }
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Timed out waiting for stack " + stackName);
    }

    private static void waitForStackDeletion(CloudFormationAsyncClient cfnClient, String stackName) {
        int maxAttempts = 60;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                DescribeStacksResponse resp = cfnClient.describeStacks(DescribeStacksRequest.builder()
                    .stackName(stackName)
                    .build()).join();
                if (resp.stacks().isEmpty()) {
                    return;
                }
                StackStatus status = resp.stacks().get(0).stackStatus();
                if (status == StackStatus.DELETE_COMPLETE) {
                    return;
                }
                Thread.sleep(5000);
            } catch (CompletionException e) {
                // Stack not found means deleted
                return;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }

    private static void emptyBucket(S3AsyncClient s3Client, String bucketName) {
        try {
            ListObjectsV2Response listResp = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build()).join();
            for (S3Object obj : listResp.contents()) {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(obj.key())
                    .build()).join();
            }
            // Handle additional pages
            String token = listResp.nextContinuationToken();
            while (token != null) {
                listResp = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .continuationToken(token)
                    .build()).join();
                for (S3Object obj : listResp.contents()) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(obj.key())
                        .build()).join();
                }
                token = listResp.nextContinuationToken();
            }
            s3Client.deleteBucket(DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build()).join();
            logger.info("Emptied and deleted bucket: {}", bucketName);
        } catch (Exception e) {
            logger.warn("Could not empty/delete bucket {}: {}", bucketName, e.getMessage());
        }
    }
}
// snippet-end:[athena.java2.scenario.main]
