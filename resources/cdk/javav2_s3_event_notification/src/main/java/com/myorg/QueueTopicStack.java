// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.sns.Subscription;
import software.amazon.awscdk.services.sns.SubscriptionProtocol;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class QueueTopicStack extends Stack {
    public QueueTopicStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public QueueTopicStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Bucket.Builder.create(this, "s3EventBucket")
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build();

        Queue.Builder.create(this, "3EventQueue")
                .visibilityTimeout(Duration.seconds(10))
                .receiveMessageWaitTime(Duration.seconds(5))
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

         Queue subscriberQueue = Queue.Builder.create(this, "Subscriber")
                .visibilityTimeout(Duration.seconds(10))
                .receiveMessageWaitTime(Duration.seconds(5))
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();

        Topic notificationTopic = Topic.Builder.create(this, "3EventTopic")
                .build();

        Subscription.Builder.create(this, "3EventSubscription")
                .topic(notificationTopic)
                .endpoint(subscriberQueue.getQueueArn())
                .protocol(SubscriptionProtocol.SQS)
                .build();
    }
}
