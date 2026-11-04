// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.LimitExceededException;
import software.amazon.awssdk.services.kinesis.model.ListStreamsRequest;
import software.amazon.awssdk.services.kinesis.paginators.ListStreamsPublisher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// snippet-start:[kinesis.java2.hello.main]
/**
 * A minimal introduction that uses {@link KinesisAsyncClient} to list all
 * Kinesis data streams in the account using the {@code listStreamsPaginator}
 * for full pagination.
 */
public class HelloKinesis {

    public static void main(String[] args) {
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

        KinesisAsyncClient kinesisClient = KinesisAsyncClient.builder()
            .httpClient(httpClient)
            .overrideConfiguration(overrideConfig)
            .build();

        try {
            listStreams(kinesisClient);
        } finally {
            kinesisClient.close();
        }
    }

    /**
     * Lists all Kinesis data streams in the current account/region using the
     * asynchronous paginator.
     *
     * @param kinesisClient the {@link KinesisAsyncClient} to use
     */
    public static void listStreams(KinesisAsyncClient kinesisClient) {
        System.out.println("--- Amazon Kinesis Data Streams Hello ---");
        System.out.println("Listing all streams in your account:");

        ListStreamsRequest request = ListStreamsRequest.builder().build();
        ListStreamsPublisher publisher = kinesisClient.listStreamsPaginator(request);

        List<String> streamNames = Collections.synchronizedList(new ArrayList<>());

        try {
            CompletableFuture<Void> future = publisher.subscribe(response -> {
                if (response.streamNames() != null) {
                    for (String name : response.streamNames()) {
                        System.out.println("  Stream: " + name);
                        streamNames.add(name);
                    }
                }
            });

            future.join();

            if (streamNames.isEmpty()) {
                System.out.println("No streams found in this account/region.");
            } else {
                System.out.println("Found " + streamNames.size() + " stream(s).");
            }
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof LimitExceededException) {
                System.err.println("Request rate limit exceeded. Please retry after a delay.");
            }
            throw e;
        }
    }
}
// snippet-end:[kinesis.java2.hello.main]
