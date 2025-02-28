// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.myorg;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Arrays;

public class EntityResolutionCdkApp {
    public static void main(final String[] args) {
        App app = new App();

        new EntityResolutionCdkStack(app, "EntityResolutionCdkStack", StackProps.builder()
              // For more information, see https://docs.aws.amazon.com/cdk/latest/guide/environments.html
                .build());

        app.synth();
    }
}

