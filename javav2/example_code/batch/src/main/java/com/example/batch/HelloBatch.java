// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.batch;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryPolicy;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.batch.BatchAsyncClient;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.batch.model.KeyValuesPair;
import software.amazon.awssdk.services.batch.model.ListJobsRequest;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HelloBatch {
    private static BatchAsyncClient batchClient;

    public static void main(String[] args) {
        listJobs();

    }

    public static void listJobs() {
        ListJobsRequest listJobsRequest = ListJobsRequest.builder()
            .jobQueue("JobQueuejavav2923E863E-Z0RmiyCBqGbvApUJ")
            .jobStatus("RUNNING")  // Filter jobs by status
            .build();

        CompletableFuture<ListJobsResponse> listJobsResponseFuture = getAsyncClient().listJobs(listJobsRequest);
        listJobsResponseFuture.thenAccept(response -> {
            List<JobSummary> jobs = response.jobSummaryList();
            if (jobs.isEmpty()) {
                System.out.println("There are no running jobs.");
            } else {
                for (JobSummary job : jobs) {
                    System.out.printf("Job ID: %s, Job Name: %s, Job Status: %s%n",
                        job.jobId(), job.jobName(), job.status());
                }
            }
        }).join();
    }

    private static BatchAsyncClient getAsyncClient() {

        /*
         The `NettyNioAsyncHttpClient` class is part of the AWS SDK for Java, version 2,
         and it is designed to provide a high-performance, asynchronous HTTP client for interacting with AWS services.
         It uses the Netty framework to handle the underlying network communication and the Java NIO API to
         provide a non-blocking, event-driven approach to HTTP requests and responses.
         */
        SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
            .maxConcurrency(100)  // Increase max concurrency to handle more simultaneous connections.
            .connectionTimeout(Duration.ofSeconds(60))  // Set the connection timeout.
            .readTimeout(Duration.ofSeconds(60))  // Set the read timeout.
            .writeTimeout(Duration.ofSeconds(60))  // Set the write timeout.
            .build();

        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
            .apiCallTimeout(Duration.ofMinutes(2))  // Set the overall API call timeout.
            .apiCallAttemptTimeout(Duration.ofSeconds(90))  // Set the individual call attempt timeout.
            .retryPolicy(RetryPolicy.builder()  // Add a retry policy to handle transient errors.
                .numRetries(3)  // Number of retry attempts.
                .build())
            .build();


        if (batchClient == null) {
            batchClient = BatchAsyncClient.builder()
                .region(Region.US_EAST_1)
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return batchClient;
    }

}
