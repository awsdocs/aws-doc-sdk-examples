package com.example.photo.services;

import com.example.photo.Job;
import com.example.photo.PhotoApplicationResources;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;

@Component
public class SnsService {
    private SnsClient getClient() {
        return SnsClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(PhotoApplicationResources.REGION)
                .build();
    }

    public String createNotificationTopic(String notify, String jobId) {
        CreateTopicResponse response = getClient().createTopic(
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

        return topicArn;
    }

    public void subTextSNS(String phoneNumber, String presignedURL) {
        try {
            String message = "Your Archived images can be located here " + presignedURL;
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            PublishResponse result = getClient().publish(request);
            System.out
                    .println(result.messageId() + " Message sent. Status was " + result.sdkHttpResponse().statusCode());

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public PublishResponse pubTopic(String message) {
        PublishRequest request = PublishRequest.builder()
                .message(message)
                .topicArn(PhotoApplicationResources.topicARN)
                .build();

        PublishResponse result = getClient().publish(request);
        return result;
    }
}
