// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;

public class Javav2SqsSseKmsEncryptionApp {
    public static void main(final String[] args) {
        App app = new App();
        new KmsQueueStack(app, "KmsQueueStack", StackProps.builder().build());
        app.synth();
    }
}

