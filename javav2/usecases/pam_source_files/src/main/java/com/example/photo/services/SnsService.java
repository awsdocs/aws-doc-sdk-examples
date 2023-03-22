<<<<<<< Updated upstream
=======
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

>>>>>>> Stashed changes
package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

public class SnsService {
    private SnsClient getClient() {
        return SnsClient.builder()
<<<<<<< Updated upstream
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(PhotoApplicationResources.REGION)
                .build();
=======
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(PhotoApplicationResources.REGION)
            .build();
>>>>>>> Stashed changes
    }

    public String createNotificationTopic(String notify, String jobId) {
        CreateTopicResponse response = getClient().createTopic(
<<<<<<< Updated upstream
                CreateTopicRequest.builder()
                        .name(jobId + "-notify-" + notify)
                        .build());
        String topicArn = response.topicArn();

        getClient().subscribe(
                SubscribeRequest.builder()
                        .topicArn(topicArn)
                        .protocol(notify.contains("@") ? "email" : "sms")
                        .endpoint(notify)
                        .build());
=======
            CreateTopicRequest.builder()
                .name(jobId + "-notify-" + notify)
                .build());
        String topicArn = response.topicArn();

        getClient().subscribe(
            SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol(notify.contains("@") ? "email" : "sms")
                .endpoint(notify)
                .build());
>>>>>>> Stashed changes

        return topicArn;
    }

    public PublishResponse subTextSNS(String phoneNumber, String presignedURL) {
        try {
            String message = "Your Archived images can be located here " + presignedURL;
            PublishRequest request = PublishRequest.builder()
<<<<<<< Updated upstream
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();
=======
                .message(message)
                .phoneNumber(phoneNumber)
                .build();
>>>>>>> Stashed changes

            PublishResponse result = getClient().publish(request);
            System.out.println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());
            return result;
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public PublishResponse pubTopic(String message) {
        PublishRequest request = PublishRequest.builder()
<<<<<<< Updated upstream
                .message(message)
                .topicArn(PhotoApplicationResources.TOPIC_ARN)
                .build();

        return getClient().publish(request);
    }
}
=======
            .message(message)
            .topicArn(PhotoApplicationResources.TOPIC_ARN)
            .build();

        return getClient().publish(request);
    }
}
>>>>>>> Stashed changes
