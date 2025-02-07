// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.entity;

import com.example.entity.scenario.EntityResScenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.entityresolution.EntityResolutionAsyncClient;
import software.amazon.awssdk.services.entityresolution.model.GetIdMappingWorkflowRequest;
import software.amazon.awssdk.services.entityresolution.model.GetIdMappingWorkflowResponse;
import software.amazon.awssdk.services.entityresolution.model.ListIdMappingJobsRequest;
import software.amazon.awssdk.services.entityresolution.model.ListMatchingWorkflowsRequest;
import software.amazon.awssdk.services.entityresolution.model.ListMatchingWorkflowsResponse;
import software.amazon.awssdk.services.entityresolution.model.ListSchemaMappingsRequest;
import software.amazon.awssdk.services.entityresolution.model.ResourceNotFoundException;
import software.amazon.awssdk.services.entityresolution.paginators.ListIdMappingJobsPublisher;
import software.amazon.awssdk.services.entityresolution.paginators.ListMatchingWorkflowsPublisher;
import software.amazon.awssdk.services.entityresolution.paginators.ListSchemaMappingsPublisher;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

// snippet-start:[entityres.java2_hello.main]
public class HelloEntityResoultion {

    private static final Logger logger = LoggerFactory.getLogger(HelloEntityResoultion.class);

    private static EntityResolutionAsyncClient entityResolutionAsyncClient;
    public static void main(String[] args) {
        listMatchingWorkflows();
    }

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

    public static void listMatchingWorkflows() {
        ListMatchingWorkflowsRequest request = ListMatchingWorkflowsRequest.builder().build();

        ListMatchingWorkflowsPublisher paginator =
            getResolutionAsyncClient().listMatchingWorkflowsPaginator(request);

        // Iterate through the paginated results asynchronously
        CompletableFuture<Void> future = paginator.subscribe(response -> {
            response.workflowSummaries().forEach(workflow ->
                logger.info("Matching Workflow Name: " + workflow.workflowName())
            );
        });

        // Wait for the asynchronous operation to complete
        future.join();
    }
}
// snippet-end:[entityres.java2_hello.main]
