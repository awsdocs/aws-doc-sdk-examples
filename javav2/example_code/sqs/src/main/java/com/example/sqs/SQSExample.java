//snippet-sourcedescription:[SQSExample.java demonstrates how to create, list and delete queues.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
// snippet-start:[sqs.java2.sqs_example.complete]
/*
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

import software.amazon.awssdk.regions.Region;
// snippet-start:[sqs.java2.sqs_example.import]
import software.amazon.awssdk.services.sqs.SqsClient;
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
// snippet-end:[sqs.java2.sqs_example.import]

import java.util.List;

// snippet-start:[sqs.java2.sqs_example.main]
public class SQSExample {

    public static void main(String[] args) {
        String queueName = "queue" + System.currentTimeMillis();
        SqsClient sqsClient = SqsClient.builder().region(Region.US_WEST_2).build();

        System.out.println("\nCreate Queue");
        // snippet-start:[sqs.java2.sqs_example.create_queue]
        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder().queueName(queueName).build();
        sqsClient.createQueue(createQueueRequest);
        // snippet-end:[sqs.java2.sqs_example.create_queue]

        System.out.println("\nGet queue url");
        // snippet-start:[sqs.java2.sqs_example.get_queue]
        GetQueueUrlResponse getQueueUrlResponse =
                sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build());
        String queueUrl = getQueueUrlResponse.queueUrl();
        System.out.println(queueUrl);
        // snippet-end:[sqs.java2.sqs_example.get_queue]


        System.out.println("\nList Queues");
        // snippet-start:[sqs.java2.sqs_example.list_queues]
        String prefix = "que";
        ListQueuesRequest listQueuesRequest = ListQueuesRequest.builder().queueNamePrefix(prefix).build();
        ListQueuesResponse listQueuesResponse = sqsClient.listQueues(listQueuesRequest);
        for (String url : listQueuesResponse.queueUrls()) {
        	System.out.println(url);
        }
        // snippet-end:[sqs.java2.sqs_example.list_queues]

        // List queues with filters
        String name_prefix = "queue";
        ListQueuesRequest filterListRequest = ListQueuesRequest.builder()
        		.queueNamePrefix(name_prefix).build();

        ListQueuesResponse listQueuesFilteredResponse = sqsClient.listQueues(filterListRequest);
        System.out.println("Queue URLs with prefix: " + name_prefix);
        for (String url : listQueuesFilteredResponse.queueUrls()) {
            System.out.println(url);
        }

        System.out.println("\nSend message");
        // snippet-start:[sqs.java2.sqs_example.send_message]
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("Hello world!")
                .delaySeconds(10)
                .build());
        // snippet-end:[sqs.java2.sqs_example.send_message]

        System.out.println("\nSend multiple messages");
        // snippet-start:[sqs.java2.sqs_example.send__multiple_messages]
        SendMessageBatchRequest sendMessageBatchRequest = SendMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(SendMessageBatchRequestEntry.builder().id("id1").messageBody("Hello from msg 1").build(),
                        SendMessageBatchRequestEntry.builder().id("id2").messageBody("msg 2").delaySeconds(10).build())
                .build();
        sqsClient.sendMessageBatch(sendMessageBatchRequest);
        // snippet-end:[sqs.java2.sqs_example.send__multiple_messages]


        System.out.println("\nReceive messages");
        // snippet-start:[sqs.java2.sqs_example.retrieve_messages]
        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
        List<Message> messages= sqsClient.receiveMessage(receiveMessageRequest).messages();
        // snippet-end:[sqs.java2.sqs_example.retrieve_messages]


        System.out.println("\nChange Message Visibility");
        for (Message message : messages) {
            ChangeMessageVisibilityRequest req = ChangeMessageVisibilityRequest.builder().queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle()).visibilityTimeout(100).build();
            sqsClient.changeMessageVisibility(req);
        }


        System.out.println("\nDelete Messages");
        // snippet-start:[sqs.java2.sqs_example.delete_message]
        for (Message message : messages) {
            DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build();
            sqsClient.deleteMessage(deleteMessageRequest);
        }
        // snippet-end:[sqs.java2.sqs_example.delete_message]

        System.out.println("\nDelete Queue");
        // snippet-start:[sqs.java2.sqs_example.delete_queue]
        DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder().queueUrl(queueUrl).build();
        sqsClient.deleteQueue(deleteQueueRequest);
        // snippet-end:[sqs.java2.sqs_example.delete_queue]
    }
}
// snippet-end:[sqs.java2.sqs_example.main]
// snippet-end:[sqs.java2.sqs_example.complete]