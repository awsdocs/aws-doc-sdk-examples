package com.example.photo.services;

import com.example.photo.PhotoApplicationResources;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

@Component
public class SnsService {
    private SnsClient getClient() {
        return SnsClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(PhotoApplicationResources.REGION)
                .build();
    }

    public String createNotificationTopic(String notify) {
        CreateTopicResponse response = getClient().createTopic(CreateTopicRequest.builder().build());
        String topicArn = response.topicArn();

        getClient().subscribe(
                SubscribeRequest.builder()
                        .topicArn(topicArn)
                        .protocol(notify.contains("@") ? "email" : "sms")
                        .endpoint(notify)
                        .build());

        return topicArn;
    }
}
