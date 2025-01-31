// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class Javav2S3EventNotificationApp {
    public static void main(final String[] args) {
        App app = new App();

        new DirectTargetStack(app, "direct-target", StackProps.builder()
                .build());
        new EventBridgeStack(app, "event-bridge", StackProps.builder()
                .build());
        new QueueTopicStack(app, "queue-topic", StackProps.builder()
                .build());
        app.synth();
    }
}