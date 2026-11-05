// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.athena;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.athena.AthenaAsyncClient;
import software.amazon.awssdk.services.athena.model.InvalidRequestException;
import software.amazon.awssdk.services.athena.model.ListWorkGroupsRequest;
import software.amazon.awssdk.services.athena.model.WorkGroupSummary;
import software.amazon.awssdk.services.athena.paginators.ListWorkGroupsPublisher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Hello Athena - Lists all workgroups in the account using the async paginator.
 */
// snippet-start:[athena.java2.hello.main]
public class HelloAthena {
    private static final Logger logger = LoggerFactory.getLogger(HelloAthena.class);

    public static void main(String[] args) {
        listWorkGroups();
    }

    /**
     * Lists all Athena workgroups using the async paginator.
     */
    public static void listWorkGroups() {
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
            .maxConcurrency(100)
            .connectionTimeout(Duration.ofSeconds(60))
            .readTimeout(Duration.ofSeconds(60))
            .writeTimeout(Duration.ofSeconds(60))
            .build();

        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMinutes(2))
            .apiCallAttemptTimeout(Duration.ofSeconds(90))
            .retryStrategy(RetryMode.STANDARD)
            .build();

        AthenaAsyncClient athenaAsyncClient = AthenaAsyncClient.builder()
            .httpClient(httpClient)
            .overrideConfiguration(overrideConfig)
            .build();

        try {
            logger.info("Listing Athena workgroups...");
            ListWorkGroupsRequest request = ListWorkGroupsRequest.builder().build();
            ListWorkGroupsPublisher paginator = athenaAsyncClient.listWorkGroupsPaginator(request);

            List<WorkGroupSummary> allWorkGroups = new ArrayList<>();
            CompletableFuture<Void> future = paginator.subscribe(response -> {
                allWorkGroups.addAll(response.workGroups());
            });
            future.join();

            for (WorkGroupSummary wg : allWorkGroups) {
                logger.info("Workgroup: {} ({})", wg.name(), wg.stateAsString());
            }
            logger.info("Found {} workgroups.", allWorkGroups.size());

        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof InvalidRequestException) {
                logger.error("Invalid request: {}", cause.getMessage());
            } else {
                logger.error("Error listing workgroups: {}", cause.getMessage());
            }
            throw e;
        } finally {
            // Closing the service client also closes the SdkAsyncHttpClient
            // passed to its builder, so the HTTP client needs no separate close().
            athenaAsyncClient.close();
        }
    }
}
// snippet-end:[athena.java2.hello.main]
