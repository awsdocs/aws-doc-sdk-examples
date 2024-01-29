// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.deploy;

// snippet-start:[codedeploy.java2.create_app.main]
// snippet-start:[codedeploy.java2.create_app.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import software.amazon.awssdk.services.codedeploy.model.CodeDeployException;
import software.amazon.awssdk.services.codedeploy.model.ComputePlatform;
import software.amazon.awssdk.services.codedeploy.model.CreateApplicationRequest;
import software.amazon.awssdk.services.codedeploy.model.CreateApplicationResponse;
// snippet-end:[codedeploy.java2.create_app.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateApplication {
    public static void main(String[] args) {

        final String usage = """

                Usage:
                    <appName>\s

                Where:
                    appName - The name of the application.\s
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String appName = args[0];
        Region region = Region.US_EAST_1;
        CodeDeployClient deployClient = CodeDeployClient.builder()
                .region(region)
                .build();

        createApp(deployClient, appName);
        deployClient.close();
    }

    public static void createApp(CodeDeployClient deployClient, String appName) {
        try {
            CreateApplicationRequest applicationRequest = CreateApplicationRequest.builder()
                    .applicationName(appName)
                    .computePlatform(ComputePlatform.SERVER)
                    .build();

            CreateApplicationResponse applicationResponse = deployClient.createApplication(applicationRequest);
            String appId = applicationResponse.applicationId();
            System.out.println("The application ID is " + appId);

        } catch (CodeDeployException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[codedeploy.java2.create_app.main]
