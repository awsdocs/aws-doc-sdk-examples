/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

//snippet-start:[sns.java2.PriceUpdateExample.import]
import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.IamEffect;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sts.StsClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//snippet-end:[sns.java2.PriceUpdateExample.import]

//snippet-start:[sns.java2.PriceUpdateExample.main]
//snippet-start:[sns.java2.PriceUpdateExample.display]
//snippet-start:[sns.java2.PriceUpdateExample.full]
public class PriceUpdateExample {
    public final static SnsClient snsClient = SnsClient.create();
    public final static SqsClient sqsClient = SqsClient.create();

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "    <topicName> <wholesaleQueueFifoName> <retailQueueFifoName> <analyticsQueueName>\n\n" +
                "Where:\n" +
                "   fifoTopicName - The name of the FIFO topic that you want to create. \n\n" +
                "   wholesaleQueueARN - The name of a SQS FIFO queue that will be created for the wholesale consumer. \n\n" +
                "   retailQueueARN - The name of a SQS FIFO queue that will created for the retail consumer. \n\n" +
                "   analyticsQueueARN - The name of a SQS standard queue that will be created for the analytics consumer. \n\n";
        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        final String fifoTopicName = args[0];
        final String wholeSaleQueueName = args[1];
        final String retailQueueName = args[2];
        final String analyticsQueueName = args[3];

        // For convenience, the QueueData class holds metadata about a queue: ARN, URL, name and type.
        List<QueueData> queues = List.of(
                new QueueData(wholeSaleQueueName, QueueType.FIFO),
                new QueueData(retailQueueName, QueueType.FIFO),
                new QueueData(analyticsQueueName, QueueType.Standard));


        // Create queues.
        createQueues(queues);

        // Create a topic.
        String topicARN = createFIFOTopic(fifoTopicName);

        // Subscribe each queue to the topic.
        subscribeQueues(queues, topicARN);

        // Allow the newly created topic to send messages to the queues.
        addAccessPolicyToQueuesFINAL(queues, topicARN);

        // Publish a sample price update message with payload.
        publishPriceUpdate(topicARN, "{\"product\": 214, \"price\": 79.99}", "Consumables");

        // Clean up resources.
        deleteSubscriptions(queues);
        deleteQueues(queues);
        deleteTopic(topicARN);
    }
//snippet-end:[sns.java2.PriceUpdateExample.main]

    public static String createFIFOTopic(String topicName) {
        try {
            // Create a FIFO topic by using the SNS service client.
            Map<String, String> topicAttributes = Map.of(
                    "FifoTopic", "true",
                    "ContentBasedDeduplication", "false");

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

    public static void subscribeQueues(List<QueueData> queues, String topicARN) {
        queues.forEach(queue -> {
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .topicArn(topicARN)
                    .endpoint(queue.queueARN)
                    .protocol("sqs")
                    .build();

            // Subscribe to the endpoint by using the SNS service client.
            // Only Amazon SQS queues can receive notifications from an Amazon SNS FIFO topic.
            SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
            System.out.println("The queue [" + queue.queueARN + "] subscribed to the topic [" + topicARN + "]");
            queue.subscriptionARN = subscribeResponse.subscriptionArn();
        });
    }

    public static void publishPriceUpdate(String topicArn, String payload, String groupId) {

        try {
            // Create and publish a message that updates the wholesale price.
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

            final PublishResponse response = snsClient.publish(pubRequest);
            System.out.println(response.messageId());
            System.out.println(response.sequenceNumber());
            System.out.println("Message was published to " + topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
//snippet-end:[sns.java2.PriceUpdateExample.display]

    public static void createQueues(List<QueueData> queueData) {
        queueData.forEach(queue -> {

            Boolean isFifoQueue = queue.queueType.equals(QueueType.FIFO) ? Boolean.TRUE : Boolean.FALSE;

            CreateQueueResponse response;
            if (isFifoQueue) {
                response = sqsClient.createQueue(r -> r
                        .queueName(queue.queueName)
                        .attributes(Map.of(
                                QueueAttributeName.FIFO_QUEUE, "true")));
            } else {
                response = sqsClient.createQueue(r -> r
                        .queueName(queue.queueName));
            }
            queue.queueURL = response.queueUrl();
            queue.queueARN = sqsClient.getQueueAttributes(b -> b
                    .queueUrl(queue.queueURL)
                    .attributeNames(QueueAttributeName.QUEUE_ARN)).attributes().get(QueueAttributeName.QUEUE_ARN);
        });
    }

    public static void addAccessPolicyToQueuesFINAL(List<QueueData> queues, String topicARN){
        String account;
        try (StsClient stsClient = StsClient.create()) {
            account = stsClient.getCallerIdentity().account();
        }
        queues.forEach(queue -> {
            IamPolicy policy = IamPolicy.builder()
                    .addStatement(b -> b  // Allow account user to send messages to the queue.
                            .effect(IamEffect.ALLOW)
                            .addPrincipal(IamPrincipalType.AWS, account)
                            .addAction("SQS:*")
                            .addResource(queue.queueARN))
                    .addStatement(b -> b  // Allow the SNS FIFO topic to send messages to the queue.
                            .effect(IamEffect.ALLOW)
                            .addPrincipal(IamPrincipalType.AWS, "*")
                            .addAction("SQS:SendMessage")
                            .addResource(queue.queueARN)
                            .addCondition(b1 -> b1
                                    .operator(IamConditionOperator.ARN_LIKE)
                                    .key("aws:SourceArn").value(topicARN)))
                    .build();
            sqsClient.setQueueAttributes(b -> b
                    .queueUrl(queue.queueURL)
                    .attributes(Map.of(
                            QueueAttributeName.POLICY,
                            policy.toJson())));
        });
    }

    public static void deleteSubscriptions(List<QueueData> queues) {
        queues.forEach(queue -> snsClient.unsubscribe(r -> r.subscriptionArn(queue.subscriptionARN)));
    }

    public static void deleteQueues(List<QueueData> queues) {
        queues.forEach(queue ->
                sqsClient.deleteQueue(b -> b.queueUrl(queue.queueURL))
        );
    }

    public static void deleteTopic(String topicArn) {
        snsClient.deleteTopic(b -> b.topicArn(topicArn).build());
    }

    public enum QueueType {
        Standard, FIFO
    }

    public static class QueueData {
        QueueType queueType;
        String queueName;
        String queueURL;
        String queueARN;
        String subscriptionARN;
        Message testMessage;

        public QueueData(String queueName, QueueType queueType) {
            this.queueType = queueType;
            this.queueName = queueName;
        }
    }
}
//snippet-end:[sns.java2.PriceUpdateExample.full]