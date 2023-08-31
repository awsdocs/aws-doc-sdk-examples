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
import software.amazon.awssdk.regions.Region;
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

import java.util.HashMap;
import java.util.List;
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

        final String fifoTopicName = args[0];
        final String wholesaleQueueARN = args[1];
        final String retailQueueARN = args[2];
        final String analyticsQueueARN = args[3];
        String wholeSaleQueueName = "wholesaleQueue.fifo";
        String retailQueueName = "retailQueue.fifo";
        String analyticsQueueName = "analyticsQueue";
        List<String> queueNames = List.of(analyticsQueueName, retailQueueName, wholeSaleQueueName);

        String wholesSaleQueueFifoARN = createQueue(wholeSaleQueueName, QueueType.FIFO);
        String retailQueueFifoARN = createQueue(retailQueueName, QueueType.FIFO);
        String analyticsStandardQueue = createQueue(analyticsQueueName, QueueType.Standard);

        final SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();


        // create a topic to publish to
        String topicARN = createFIFOTopic(snsClient, fifoTopicName);

        // Add the subscriptions
        String wholesaleQueueSubscriptionARN = subscribeQueue(snsClient, topicARN, wholesSaleQueueFifoARN);
        String retailQueueSubscriptionARN = subscribeQueue(snsClient, topicARN, retailQueueFifoARN);
        String analyticsQueueSubscriptionARN = subscribeQueue(snsClient, topicARN, analyticsStandardQueue);
        // allow the newly created topic to send messages to the queue
        allowTopicToSendToQueues(queueNames, topicARN);

        // publish a sample price update message with payload
        publishPriceUpdate(snsClient, topicARN, "{\"product\": 214, \"price\": 79.99}", "Consumables");
/*
        try {
            long millisToWait = 10000L;
            System.out.println("Waiting for " + millisToWait + " milliseconds before unsubscribing the queues and deleting the topic");
            Thread.sleep(millisToWait);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
*/
        deleteSubscriptions(snsClient, List.of(wholesaleQueueSubscriptionARN, retailQueueSubscriptionARN, analyticsQueueSubscriptionARN));
        deleteQueues(queueNames);
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

    public static String subscribeQueue(SnsClient snsClient, String topicARN, String queueARN) {

        try {
            // Subscribe to the endpoint by using the SNS service client.
            // Only Amazon SQS queues can receive notifications from an Amazon SNS FIFO topic.
            SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                    .topicArn(topicARN)
                    .endpoint(queueARN)
                    .protocol("sqs")
                    .build();

            SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
            System.out.println("The queue [" + queueARN + "] subscribed to the topic [" + topicARN + "]");
            return subscribeResponse.subscriptionArn();

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return topicARN;
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

            final PublishResponse response = snsClient.publish(pubRequest);
            System.out.println(response.messageId());
            System.out.println(response.sequenceNumber());
            System.out.println("Message was published to " + topicArn);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void deleteSubscriptions(SnsClient snsClient, List<String> arns) {
        arns.forEach(a -> snsClient.unsubscribe(r -> r.subscriptionArn(a)));
    }

    public static void deleteTopic(SnsClient snsClient, String topicArn) {
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

    public static void allowTopicToSendToQueues(List<String> queueNames, String topicARN) {
        try (SqsClient sqsClient = SqsClient.create()) {
            queueNames.forEach(q -> {
                String queueUrl = sqsClient.getQueueUrl(b -> b.queueName(q)).queueUrl();
                Map<QueueAttributeName, String> attributes = sqsClient.getQueueAttributes(b -> b
                                .queueUrl(queueUrl)
                                .attributeNames(
                                        QueueAttributeName.POLICY,
                                        QueueAttributeName.QUEUE_ARN))
                        .attributes();
                String queueARN = attributes.get(QueueAttributeName.QUEUE_ARN);
                String policy = attributes.get(QueueAttributeName.POLICY);
                String queuePolicy = addQueueAccessPolicy(topicARN, queueARN, policy);
                sqsClient.setQueueAttributes(b -> b.queueUrl(queueUrl).attributes(Map.of(QueueAttributeName.POLICY, queuePolicy)));
                System.out.println(policy);
                System.out.println(queuePolicy);
            });
        }
    }

    public static String createQueue(String queueName, QueueType queueType) {
        Boolean isFifoQueue = queueType.equals(QueueType.FIFO) ? Boolean.TRUE : Boolean.FALSE;

        try (SqsClient sqsClient = SqsClient.create()) {
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

    private static void deleteQueues(List<String> queueNames){
        try (SqsClient sqsClient = SqsClient.create()) {
            queueNames.forEach(queueName -> {
                String url = sqsClient.getQueueUrl(b -> b.queueName(queueName)).queueUrl();
                sqsClient.deleteQueue(b -> b.queueUrl(url));
            });
        }
    }

    public static enum QueueType {
        Standard, FIFO
    }
}
