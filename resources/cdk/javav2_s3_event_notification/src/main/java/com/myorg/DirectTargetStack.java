// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.notifications.SnsDestination;
import software.amazon.awscdk.services.s3.notifications.SqsDestination;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;

public class DirectTargetStack extends Stack {
    public DirectTargetStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public DirectTargetStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        final Bucket bucket = Bucket.Builder.create(this, "s3EventNotificationBucket")
                .removalPolicy(RemovalPolicy.DESTROY)
                .autoDeleteObjects(true)
                .build();

        final Queue queue = Queue.Builder.create(this, "3EventNotificationQueue")
                .visibilityTimeout(Duration.seconds(10))
                .receiveMessageWaitTime(Duration.seconds(5))
                .build();

        bucket.addEventNotification(EventType.OBJECT_REMOVED_DELETE, new SqsDestination(queue));
    }
}
