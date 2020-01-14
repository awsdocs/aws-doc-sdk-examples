//snippet-sourcedescription:[KinesisStreamRxJavaEx.java demonstrates how to use the Rx Java library to simplify processing of Kinesis streams.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[kinesis]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[kinesis.java2.stream_rx_example.complete]
// snippet-start:[kinesis.java2.stream_rx_example.import]

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import io.reactivex.Flowable;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StartingPosition;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardEvent;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardRequest;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardResponseHandler;
import software.amazon.awssdk.utils.AttributeMap;
// snippet-end:[kinesis.java2.stream_rx_example.import]

public class KinesisStreamRxJavaEx {

    private static final String CONSUMER_ARN =  "arn:aws:kinesis:us-east-1:1234567890:stream/stream-name/consumer/consumer-name:1234567890";

    /**
     * Uses RxJava via the onEventStream lifecycle method. This gives you full access to the publisher which can be used
     * to create an Rx Flowable.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_RxJava(KinesisAsyncClient client, SubscribeToShardRequest request) {

        // snippet-start:[kinesis.java2.stream_rx_example.event_stream]
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .onEventStream(p -> Flowable.fromPublisher(p)
                                        .ofType(SubscribeToShardEvent.class)
                                        .flatMapIterable(SubscribeToShardEvent::records)
                                        .limit(1000)
                                        .buffer(25)
                                        .subscribe(e -> System.out.println("Record batch = " + e)))
            .build();
        // snippet-end:[kinesis.java2.stream_rx_example.event_stream]
        return client.subscribeToShard(request, responseHandler);

    }

    /**
     * Since a Flowable is also a publisher, the publisherTransformer method integrates nicely with RxJava. Note that
     * you must adapt to an SdkPublisher.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_OnEventStream_RxJava(KinesisAsyncClient client, SubscribeToShardRequest request) {
        // snippet-start:[kinesis.java2.stream_rx_example.publish_transform]
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .publisherTransformer(p -> SdkPublisher.adapt(Flowable.fromPublisher(p).limit(100)))
            .build();
        // snippet-end:[kinesis.java2.stream_rx_example.publish_transform]
        return client.subscribeToShard(request, responseHandler);
    }

    public static void main(String[] args) {







        KinesisAsyncClient client = KinesisAsyncClient.create();

        SubscribeToShardRequest request = SubscribeToShardRequest.builder()
                .consumerARN(CONSUMER_ARN)
                .shardId("shardId-000000000000")
                .startingPosition(StartingPosition.builder().type(ShardIteratorType.LATEST).build())
                .build();

        responseHandlerBuilder_RxJava(client, request).join();

        client.close();
    }


}
 
// snippet-end:[kinesis.java2.stream_rx_example.complete]
