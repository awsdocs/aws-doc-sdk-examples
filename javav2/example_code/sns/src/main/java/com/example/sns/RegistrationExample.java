//snippet-sourcedescription:[RegistrationExample.java demonstrates how to create a platform application object for one of the supported push notification services.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Simple Notification Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.sns;

//snippet-start:[sns.java2.reg.endpoint.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointRequest;
import software.amazon.awssdk.services.sns.model.CreatePlatformEndpointResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
//snippet-end:[sns.java2.reg.endpoint.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * In addition, create a platform application using the AWS Management Console. See this doc topic:
 *
 * https://docs.aws.amazon.com/sns/latest/dg/mobile-push-send-register.html
 *
 * Without the values created by following the previous link, this code examples does not work.
 */

//snippet-start:[sns.java2.reg.endpoint.main]
public class RegistrationExample {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "    <token>\n\n" +
            "Where:\n" +
            "   token - The name of the FIFO topic. \n\n" +
            "   platformApplicationArn - The ARN value of platform application. You can get this value from the AWS Management Console. \n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String token = args[0];
        String platformApplicationArn = args[1];
        SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createEndpoint(snsClient, token, platformApplicationArn);
    }

    public static void createEndpoint(SnsClient snsClient, String token, String platformApplicationArn){

        System.out.println("Creating platform endpoint with token " + token);

        try {
            CreatePlatformEndpointRequest endpointRequest = CreatePlatformEndpointRequest.builder()
                .token(token)
                .platformApplicationArn(platformApplicationArn)
                .build();

            CreatePlatformEndpointResponse response = snsClient.createPlatformEndpoint(endpointRequest);
            System.out.println("The ARN of the endpoint is " + response.endpointArn());
        } catch ( SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
//snippet-end:[sns.java2.reg.endpoint.main]
