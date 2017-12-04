/*
 * Copyright 2011-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sqs;

import software.amazon.awssdk.core.regions.Region;
import software.amazon.awssdk.services.sqs.SQSClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.ListQueuesRequest;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;

public class SQSExample {

    public static void main(String[] args) {
        String queueName = "queue" + System.currentTimeMillis();
        SQSClient sqsClient = SQSClient.builder().region(Region.US_WEST_2).build();

        System.out.println("\nCreate Queue");
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName(queueName).build();
        sqsClient.createQueue(createQueueRequest);

        System.out.println("\nGet queue url");
        GetQueueUrlResponse getQueueUrlResponse =
                sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        String queueUrl = getQueueUrlResponse.queueUrl();
        System.out.println(queueUrl);


        System.out.println("\nList Queues");
        String prefix = "que";
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(prefix).build();
        ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
        for (String url : listQueuesResponse.queueUrls()) {
        	System.out.println(url);
        }

        System.out.println("\nSend message");
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("Hello world!")
                .delaySeconds(10)
                .build());

        System.out.println("\nSend multiple messages");
        SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
                        SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
                .build();
        sqsClient.sendMessageBatch(sendMessageBatchRequest);


        System.out.println("\nReceive messages");
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
        List<Message> messages= sqsClient.receiveMessage(receiveMessageRequest).messages();


        System.out.println("\nChange Message Visibility");
        for (Message message : messages) {
            ChangeMessageVisibilityRequest req = ChangeMessageVisibilityRequest.builder().queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle()).visibilityTimeout(100).build();
            sqsClient.changeMessageVisibility(req);
        }


        System.out.println("\nDelete Messages");
        for (Message message : messages) {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteMessageRequest);
        }

        System.out.println("\nDelete Queue");
        DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder().queueUrl(queueUrl).build();
        sqsClient.deleteQueue(deleteQueueRequest);
    }
}
