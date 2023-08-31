/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sns;

import software.amazon.awssdk.policybuilder.iam.IamConditionOperator;
import software.amazon.awssdk.policybuilder.iam.IamEffect;
import software.amazon.awssdk.policybuilder.iam.IamPolicy;
import software.amazon.awssdk.policybuilder.iam.IamPolicyWriter;
import software.amazon.awssdk.policybuilder.iam.IamPrincipalType;
import software.amazon.awssdk.policybuilder.iam.IamStatement;
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
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sts.StsClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FIFOTopicStandardQueue {
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

        //  Group items for iterating.
        List<String> queueNames = List.of(analyticsQueueName, retailQueueName, wholeSaleQueueName);

        //  Create queues.
        String wholesSaleQueueFifoARN = createQueue(wholeSaleQueueName, QueueType.FIFO);
        String retailQueueFifoARN = createQueue(retailQueueName, QueueType.FIFO);
        String analyticsStandardQueueARN = createQueue(analyticsQueueName, QueueType.Standard);

        //  Group queue URLs and ARNs for iterating.
        List<String> queueURLs = getQueueURLs(queueNames);
        List<String> queueARNs = List.of(wholesSaleQueueFifoARN, retailQueueFifoARN, analyticsStandardQueueARN);

        // Create a topic.
        String topicARN = createFIFOTopic(fifoTopicName);

        // Subscribe each queue to the topic.
        List<String> subscriptionARNs = subscribeQueues(topicARN, queueARNs);

        // Allow the newly created topic to send messages to the queues.
        allowTopicToSendToQueues(queueURLs, topicARN);

        // Publish a sample price update message with payload.
        publishPriceUpdate(topicARN, "{\"product\": 214, \"price\": 79.99}", "Consumables");

        // Clean up resources.
        deleteSubscriptions(subscriptionARNs);
        deleteQueues(queueURLs);
        deleteTopic(topicARN);
    }

    public static String createFIFOTopic(String topicName) {

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

    public static List<String> subscribeQueues(String topicARN, List<String> queueARNs) {
        List<String> subscriptionARNs = new ArrayList<>();
        queueARNs.forEach(queueARN -> {
            // Subscribe to the endpoint by using the SNS service client.
            // Only Amazon SQS queues can receive notifications from an Amazon SNS FIFO topic.
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .topicArn(topicARN)
                    .endpoint(queueARN)
                    .protocol("sqs")
                    .build();

            SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
            System.out.println("The queue [" + queueARN + "] subscribed to the topic [" + topicARN + "]");
            subscriptionARNs.add(subscribeResponse.subscriptionArn());
        });
        return subscriptionARNs;
    }

    public static void publishPriceUpdate(String topicArn, String payload, String groupId) {

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

            final PublishResponse response = snsClient.publish(pubRequest);
            System.out.println(response.messageId());
            System.out.println(response.sequenceNumber());
            System.out.println("Message was published to " + topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void deleteSubscriptions(List<String> arns) {
        arns.forEach(a -> snsClient.unsubscribe(r -> r.subscriptionArn(a)));
    }

    public static void deleteTopic(String topicArn) {
        snsClient.deleteTopic(b -> b.topicArn(topicArn).build());
    }

    public static String addQueueAccessPolicy(String topicARN, String queueARN, String existingPolicyAsString) {

        IamPolicy existingPolicy = IamPolicy.fromJson(existingPolicyAsString);

        IamStatement sendMessageStatement = IamStatement.builder()
                .effect(IamEffect.ALLOW)
                .addPrincipal(IamPrincipalType.AWS, "*")
                .addAction("SQS:SendMessage")
                .addResource(queueARN)
                .addCondition(b1 -> b1
                        .operator(IamConditionOperator.ARN_LIKE)
                        .key("aws:SourceArn").value(topicARN))
                .build();

        IamPolicy newPolicy = existingPolicy.copy(p -> p.addStatement(sendMessageStatement));


        return newPolicy.toJson(IamPolicyWriter.builder()
                .prettyPrint(true).build());
    }

    public static void allowTopicToSendToQueues(List<String> queueURLs, String topicARN) {
        queueURLs.forEach(queueURL -> {
            Map<QueueAttributeName, String> attributes = sqsClient.getQueueAttributes(b -> b
                            .queueUrl(queueURL)
                            .attributeNames(
                                    QueueAttributeName.POLICY,
                                    QueueAttributeName.QUEUE_ARN))
                    .attributes();
            String queueARN = attributes.get(QueueAttributeName.QUEUE_ARN);
            String policy = attributes.get(QueueAttributeName.POLICY);
            String queuePolicy = addQueueAccessPolicy(topicARN, queueARN, policy);
            sqsClient.setQueueAttributes(b -> b.queueUrl(queueURL).attributes(Map.of(QueueAttributeName.POLICY, queuePolicy)));
            System.out.println(policy);
            System.out.println(queuePolicy);
        });
    }

    public static String createQueue(String queueName, QueueType queueType) {
        Boolean isFifoQueue = queueType.equals(QueueType.FIFO) ? Boolean.TRUE : Boolean.FALSE;

        CreateQueueResponse response;
        if (isFifoQueue) {
            response = sqsClient.createQueue(r -> r
                    .queueName(queueName)
                    .attributes(Map.of(
                            QueueAttributeName.FIFO_QUEUE, "true")));
        } else {
            response = sqsClient.createQueue(r -> r
                    .queueName(queueName));
        }
        String queueUrl = response.queueUrl();
        String queueArn = sqsClient.getQueueAttributes(b -> b
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN)).attributes().get(QueueAttributeName.QUEUE_ARN);
        String userPolicy = getUserQueuePolicy(queueArn);
        sqsClient.setQueueAttributes(b -> b
                .queueUrl(queueUrl)
                .attributes(Map.of(QueueAttributeName.POLICY, userPolicy)));
        return queueArn;
    }

    private static String getUserQueuePolicy(String queueARN) {
        String account;
        try (StsClient stsClient = StsClient.create()) {
            account = stsClient.getCallerIdentity().account();
        }


        IamPolicy policy = IamPolicy.builder()
                .addStatement(b -> b
                        .effect(IamEffect.ALLOW)
                        .addPrincipal(IamPrincipalType.AWS, account)
                        .addAction("SQS:*")
                        .addResource(queueARN))
                .build();
        return policy.toJson(IamPolicyWriter.builder()
                .prettyPrint(true).build());
    }

    private static void deleteQueues(List<String> queueURLs) {
        queueURLs.forEach(queueURL ->
                sqsClient.deleteQueue(b -> b.queueUrl(queueURL))
        );
    }

    private static List<String> getQueueURLs(List<String> queueNames) {
        List<String> urls = new ArrayList<>();
        queueNames.forEach(queueName ->
                urls.add(sqsClient.getQueueUrl(b -> b.queueName(queueName)).queueUrl())
        );
        return urls;
    }

    public enum QueueType {
        Standard, FIFO
    }
}
