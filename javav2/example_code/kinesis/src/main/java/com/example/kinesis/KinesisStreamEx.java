package com.example.kinesis;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.http.Protocol;
import software.amazon.awssdk.http.SdkHttpConfigurationOption;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardEvent;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardEventStream;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardRequest;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardResponse;
import software.amazon.awssdk.services.kinesis.model.SubscribeToShardResponseHandler;
import software.amazon.awssdk.utils.AttributeMap;

public class KinesisStreamEx {
	
    /**
     * Creates a SubscribeToShardResponseHandler using the builder which lets you set each lifecycle callback separately
     * rather than implementing the interface.
     */
    private static CompletableFuture<Void> responseHandlerBuilder(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .onComplete(() -> System.out.println("All records stream successfully"))
            // Must supply some type of subscriber
            .subscriber(e -> System.out.println("Received event - " + e))
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
    
    /**
     * Using the SubscribeToShardResponseHandler.Builder and a simple Consumer of events to subscribe.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_Consumer(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .subscriber(e -> System.out.println("Received event - " + e))
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
 
    /**
     * Uses the publisherTransformer method to customize the publisher before ultimately subscribing to it.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_PublisherTransformer(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .publisherTransformer(p -> p.filter(e -> e instanceof SubscribeToShardEvent).limit(100))
            .subscriber(e -> System.out.println("Received event - " + e))
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
 
    /**
     * Creates a SubscribeToShardResponseHandler.Visitor using the builder which lets you register an event handler for
     * all events you're interested in rather than implementing the interface.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_VisitorBuilder(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler.Visitor visitor = SubscribeToShardResponseHandler.Visitor
            .builder()
            .onSubscribeToShardEvent(e -> System.out.println("Received subscribe to shard event " + e))
            .build();
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .subscriber(visitor)
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
 
    /**
     * Subscribes to the stream of events by implementing the SubscribeToShardResponseHandler.Visitor interface.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_Visitor(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler.Visitor visitor = new SubscribeToShardResponseHandler.Visitor() {
            @Override
            public void visit(SubscribeToShardEvent event) {
                System.out.println("Received subscribe to shard event " + event);
            }
        };
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .subscriber(visitor)
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
 
    /**
     * Creates a SubscribeToShardResponseHandler the classic way by implementing the interface.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_Classic(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = new SubscribeToShardResponseHandler() {
 
            @Override
            public void responseReceived(SubscribeToShardResponse response) {
                System.out.println("Receieved initial response");
            }
 
            @Override
            public void onEventStream(SdkPublisher<SubscribeToShardEventStream> publisher) {
                publisher
                    // Filter to only SubscribeToShardEvents
                    .filter(SubscribeToShardEvent.class)
                    // Flat map into a publisher of just records
                    .flatMapIterable(SubscribeToShardEvent::records)
                    // Limit to 1000 total records
                    .limit(1000)
                    // Batch records into lists of 25
                    .buffer(25)
                    // Print out each record batch
                    .subscribe(batch -> System.out.println("Record Batch - " + batch));
            }
 
            @Override
            public void complete() {
                System.out.println("All records stream successfully");
            }
 
            @Override
            public void exceptionOccurred(Throwable throwable) {
                System.err.println("Error during stream - " + throwable.getMessage());
            }

        };
        return client.subscribeToShard(request, responseHandler);
    }
    
    /**
     * Using the SubscribeToShardResponseHandler.Builder and a traditional subscriber.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_Subscriber(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .subscriber(MySubscriber::new)
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
 
    /**
     * Subscribes to the publisher using the onEventStream lifecycle callback method. This allows for greater control
     * over the publisher and allows for transformation methods on the publisher like map and buffer.
     */
    private static CompletableFuture<Void> responseHandlerBuilder_OnEventStream(KinesisAsyncClient client, SubscribeToShardRequest request) {
        SubscribeToShardResponseHandler responseHandler = SubscribeToShardResponseHandler
            .builder()
            .onError(t -> System.err.println("Error during stream - " + t.getMessage()))
            .onEventStream(p -> p.filter(SubscribeToShardEvent.class).subscribe(new MySubscriber()))
            .build();
        return client.subscribeToShard(request, responseHandler);
    }
 
    /**
     * Simple subscriber implementation that prints events and cancels the subscription after 100 events.
     */
    private static class MySubscriber implements Subscriber<SubscribeToShardEventStream> {
 
        private Subscription subscription;
        private AtomicInteger eventCount = new AtomicInteger(0);
 
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            this.subscription.request(1);
        }
 
        @Override
        public void onNext(SubscribeToShardEventStream shardSubscriptionEventStream) {
            System.out.println("Received event " + shardSubscriptionEventStream);
            if (eventCount.incrementAndGet() >= 100) {
                // You can cancel the subscription at any time if you wish to stop receiving events.
                subscription.cancel();
            }
            subscription.request(1);
        }
 
        @Override
        public void onError(Throwable throwable) {
            System.err.println("Error occurred while stream - " + throwable.getMessage());
        }
 
        @Override
        public void onComplete() {
            System.out.println("Finished streaming all events");
        }
    }

    private static final String CONSUMER_ARN =  "arn:aws:kinesis:us-east-1:052958737983:stream/foobar/consumer/shorea-consumer:1525898737";
    
    public static void main(String[] args) {
        KinesisAsyncClient clientTest = KinesisAsyncClient.builder()
                .httpClient(NettyNioAsyncHttpClient.builder()
                		.buildWithDefaults(AttributeMap.builder()
                				.put(SdkHttpConfigurationOption.TRUST_ALL_CERTIFICATES, true)
                				.put(SdkHttpConfigurationOption.PROTOCOL, Protocol.HTTP2)
                				.build()))
                .endpointOverride(URI.create("https://aws-kinesis-alpha.corp.amazon.com")).region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create("kinesis-alpha")).build();
        
        KinesisAsyncClient client = KinesisAsyncClient.create();
        
        SubscribeToShardRequest request = SubscribeToShardRequest.builder()
                .consumerARN(CONSUMER_ARN)
                .shardId("shardId-000000000000")
                .startingPosition(s -> s.type(ShardIteratorType.LATEST)).build();
        
        responseHandlerBuilder_Subscriber(client, request).join();

        client.close();
    }

}

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

