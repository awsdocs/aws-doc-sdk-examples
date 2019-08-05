//snippet-sourcedescription:[KinesisStreamReactorEx.java demonstrates how to use the Reactor library to simplify processing of Kinesis streams.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2011-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kinesis;
// snippet-start:[kinesis.java2.stream_reactor_example.complete]
// snippet-start:[kinesis.java2.stream_reactor_example.import]

import reactor.core.publisher.Flux;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StartingPosition;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardEvent;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardRequest;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardResponseHandler;

import java.util.concurrent.CompletableFuture;
// snippet-end:[kinesis.java2.stream_reactor_example.import]

/**
 *
 * @author Sergei @bsideup Egorov
 */
// snippet-start:[kinesis.java2.stream_reactor_example.main]
public class KinesisStreamReactorEx {

    private static final String CONSUMER_ARN =  "arn:aws:kinesis:us-east-1:1234567890:stream/stream-name/consumer/consumer-name:1234567890";

    /**
     * Uses Reactor via the onEventStream lifecycle method. This gives you full access to the publisher which can be used
     * to create a Flux.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_Reactor(KinesisAsyncClient client, SubscribeToShardRequest request) {

        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .onEventStream(p -> Flux.from(p)
                                    .ofType(SubscribeToShardEvent.class)
                                    .flatMapIterable(SubscribeToShardEvent::records)
                                    .limitRate(1000)
                                    .buffer(25)
                                    .subscribe(e -> System.out.println("Record batch = " + e)))
            .build();
        return client.subscribeToShard(request, responseHandler);

    }

    /**
     * Since a Flux is also a publisher, the publisherTransformer method integrates nicely with Reactor. Note that
     * you must adapt to an SdkPublisher.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_OnEventStream_Reactor(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .publisherTransformer(p -> Flux.from(p).limitRate(100).as(SdkPublisher::adapt))
            .build();
        return client.subscribeToShard(request, responseHandler);
    }

    public static void main(String[] args) {







        KinesisAsyncClient client = KinesisAsyncClient.create();

        SubscribeToShardRequest request = SubscribeToShardRequest.builder()
                .consumerARN(CONSUMER_ARN)
                .shardId("shardId-000000000000")
                .startingPosition(StartingPosition.builder().type(ShardIteratorType.LATEST).build())
                .build();

        responseHandlerBuilder_Reactor(client, request).join();

        client.close();
    }


}
 
// snippet-end:[kinesis.java2.stream_reactor_example.main]
// snippet-end:[kinesis.java2.stream_reactor_example.complete]
