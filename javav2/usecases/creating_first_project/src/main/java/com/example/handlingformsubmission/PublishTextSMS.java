/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.handlingformsubmission;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import org.springframework.stereotype.Component;

@Component("PublishTextSMS")
public class PublishTextSMS {

    public void sendMessage(String id) {

        Region region = Region.US_EAST_1;
        SnsClient snsClient = SnsClient.builder()
                .region(region)
             //   .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String message = "A new item with ID value "+ id +" was added to the DynamoDB table";
        String phoneNumber="18195765654"; //Replace with a mobile phone number

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            snsClient.publish(request);

        } catch (SnsException e) {

            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}