// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.events.EventBus;
import software.amazon.awscdk.services.events.EventPattern;
import software.amazon.awscdk.services.events.Rule;
import software.amazon.awscdk.services.events.targets.SnsTopic;
import software.amazon.awscdk.services.events.targets.SqsQueue;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class EventBridgeStack extends Stack {
    public EventBridgeStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public EventBridgeStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        final Bucket bucket = Bucket.Builder.create(this, "s3EventNotificationBucket")
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .eventBridgeEnabled(true)
                .build();

        final Queue queue = Queue.Builder.create(this, "3EventNotificationQueue")
                .visibilityTimeout(Duration.seconds(10))
                .receiveMessageWaitTime(Duration.seconds(5))
                .build();

        final Topic topic = Topic.Builder.create(this, "3EventNotificationTopic")
                .build();

        EventBus eventBus = EventBus.Builder.create(this, "3EventBus")
                .eventBusName("3EventBus")
                .build();

        EventPattern objectCreatedPattern = EventPattern.builder()
                .source(List.of("aws.s3"))
                .detailType(List.of("Object Created"))
                .detail(Map.of("bucket", List.of(bucket.getBucketName())))
                .build();

        Rule.Builder.create(this, "3EventBusRule")
                .description("3EventBusRule")
                .eventBus(eventBus)
                .eventPattern(objectCreatedPattern)
                .targets(List.of(
                        SqsQueue.Builder.create(queue).build(),
                        SnsTopic.Builder.create(topic).build())
                )
                .build();
    }
}
