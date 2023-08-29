//snippet-sourcedescription:[CreatePubFIFO.java demonstrates how to create and publish to a FIFO Amazon Simple Notification Service (Amazon SNS) topic.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Simple Notification Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FIFOTopicStandardQueue {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "    <topicName> <wholesaleQueueARN> <retailQueueARN> <analyticsQueueARN>\n\n" +
                "Where:\n" +
                "   fifoTopicName - The name of the FIFO topic that you want to create. \n\n" +
                "   wholesaleQueueARN - The ARN value of a SQS FIFO queue that you have created for the wholesale consumer. You can get this value from the AWS Management Console. \n\n" +
                "   retailQueueARN - The ARN value of a SQS FIFO queue that you have created for the retail consumer. You can get this value from the AWS Management Console. \n\n" +
                "   analyticsQueueARN - The ARN value of a SQS standard queue that you have created for the analytics consumer. You can get this value from the AWS Management Console. \n\n";
        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String fifoTopicName = args[0];
        String wholesaleQueueARN = args[1];
        String retailQueueARN = args[2];
        String analyticsQueueARN = args[3];
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // create a topic to publish to
        String topicARN = createFIFOTopic(snsClient, fifoTopicName);
        // Add the subscriptions
        subscribeQueue(snsClient, topicARN, wholesaleQueueARN);
        subscribeQueue(snsClient, topicARN, retailQueueARN);
        subscribeQueue(snsClient, topicARN, analyticsQueueARN);
        // publish a sample price update message with payload
        publishPriceUpdate(snsClient, topicARN, "{\"product\": 214, \"price\": 79.99}", "Consumables");
        deleteTopic(snsClient, topicARN);
    }

    public static String createFIFOTopic(SnsClient snsClient, String topicName) {

        try {
            // Create a FIFO topic by using the SNS service client.
            Map<String, String> topicAttributes = new HashMap<>();
            topicAttributes.put("FifoTopic", "true");
            topicAttributes.put("ContentBasedDeduplication", "false");

            CreateTopicRequest topicRequest = CreateTopicRequest.builder()
                    .name(topicName)
                    .attributes(topicAttributes)
                    .build();

            CreateTopicResponse response = snsClient.createTopic(topicRequest);
            String topicArn = response.topicArn();
            System.out.println("The topic ARN is" + topicArn);

            return topicArn;

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
    }

    public static void subscribeQueue(SnsClient snsClient, String topicARN, String queueARN) {

        try {
            // Subscribe to the endpoint by using the SNS service client.
            // Only Amazon SQS queues can receive notifications from an Amazon SNS FIFO topic.
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .topicArn(topicARN)
                    .endpoint(queueARN)
                    .protocol("sqs")
                    .build();

            snsClient.subscribe(subscribeRequest);
            System.out.println("The queue is subscribed to the topic.");

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void publishPriceUpdate(SnsClient snsClient, String topicArn, String payload, String groupId) {

        try {
            // Compose and publish a message that updates the wholesale price.
            String subject = "Price Update";
            String dedupId = UUID.randomUUID().toString();
            String attributeName = "business";
            String attributeValue = "wholesale";

            MessageAttributeValue msgAttValue = MessageAttributeValue.builder()
                    .dataType("String")
                    .stringValue(attributeValue)
                    .build();

            Map<String, MessageAttributeValue> attributes = new HashMap<>();
            attributes.put(attributeName, msgAttValue);
            PublishRequest pubRequest = PublishRequest.builder()
                    .topicArn(topicArn)
                    .subject(subject)
                    .message(payload)
                    .messageGroupId(groupId)
                    .messageDeduplicationId(dedupId)
                    .messageAttributes(attributes)
                    .build();

            snsClient.publish(pubRequest);
            System.out.println("Message was published to " + topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void deleteTopic(SnsClient snsClient, String topicArn) {
        snsClient.deleteTopic(b -> b.topicArn(topicArn).build());
    }
}
//snippet-end:[sns.java2.CreateTopicFIFO.main]