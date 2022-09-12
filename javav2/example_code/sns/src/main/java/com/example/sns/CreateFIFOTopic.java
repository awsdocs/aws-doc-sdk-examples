//snippet-sourcedescription:[CreatePubFIFO.java demonstrates how to create and publish to a FIFO Amazon Simple Notification Service (Amazon SNS) topic.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Simple Notification Service]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

//snippet-start:[sns.java2.CreateTopicFIFO.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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
//snippet-end:[sns.java2.CreateTopicFIFO.import]

public class CreateFIFOTopic {

    //snippet-start:[sns.java2.CreateTopicFIFO.main]
    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage: " +
            "    <topicArn>\n\n" +
            "Where:\n" +
            "   fifoTopicName - The name of the FIFO topic. \n\n" +
            "   fifoQueueARN - The ARN value of a SQS FIFO queue. You can get this value from the AWS Management Console. \n\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String fifoTopicName = "PriceUpdatesTopic3.fifo";
        String fifoQueueARN = "arn:aws:sqs:us-east-1:814548047983:MyPriceSQS.fifo";
        SnsClient snsClient = SnsClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createFIFO(snsClient, fifoTopicName, fifoQueueARN);
    }

    public static void createFIFO(SnsClient snsClient, String topicName, String queueARN) {

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
            System.out.println("The topic ARN is"+topicArn);

            // Subscribe to the endpoint by using the SNS service client.
            // Only Amazon SQS FIFO queues can receive notifications from an Amazon SNS FIFO topic.
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .topicArn(topicArn)
                .endpoint(queueARN)
                .protocol("sqs")
                .build();

            snsClient.subscribe(subscribeRequest);
            System.out.println("The topic is subscribed to the queue.");

            // Compose and publish a message that updates the wholesale price.
            String subject = "Price Update";
            String payload = "{\"product\": 214, \"price\": 79.99}";
            String groupId = "PID-214";
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
            System.out.println("Message was published to "+topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
//snippet-end:[sns.java2.CreateTopicFIFO.main]