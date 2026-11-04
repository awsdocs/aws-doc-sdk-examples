// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.LimitExceededException;
import software.amazon.awssdk.services.kinesis.model.ListStreamsRequest;

import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicInteger;

// snippet-start:[kinesis.java2.hello.main]
/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class HelloKinesis {

    private static final Logger logger = LoggerFactory.getLogger(HelloKinesis.class);

    public static void main(String[] args) {
        try (KinesisAsyncClient kinesisClient = KinesisAsyncClient.create()) {
            listStreams(kinesisClient);
        }
    }

    /**
     * Lists up to 10 Kinesis data streams in the current account and Region.
     *
     * @param kinesisClient the {@link KinesisAsyncClient} to use
     */
    public static void listStreams(KinesisAsyncClient kinesisClient) {
        logger.info("Hello, Amazon Kinesis! Let's list some of your streams:");
        // The request limit sets the page size. The paginator would still fetch every
        // page, so the publisher limit below stops after the first 10 stream names.
        ListStreamsRequest request = ListStreamsRequest.builder()
            .limit(10)
            .build();

        AtomicInteger count = new AtomicInteger();
        try {
            kinesisClient.listStreamsPaginator(request)
                .streamNames()
                .limit(10)
                .subscribe(name -> {
                    logger.info("  Stream: {}", name);
                    count.incrementAndGet();
                })
                .join();

            if (count.get() == 0) {
                logger.info("No streams found in your account.");
            } else {
                logger.info("Found {} stream(s).", count.get());
            }
        } catch (CompletionException e) {
            if (e.getCause() instanceof LimitExceededException) {
                logger.error("The request rate is too high. Wait briefly and then retry.");
            }
            throw e;
        }
    }
}
// snippet-end:[kinesis.java2.hello.main]
