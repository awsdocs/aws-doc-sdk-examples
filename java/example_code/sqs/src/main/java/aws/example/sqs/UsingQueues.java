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
package com.amazonaws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;

import java.util.Date;

public class UsingQueues {

    private static final String QUEUE_NAME = "testQueue" + new Date().getTime();

    public static void main(String[] args) {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Creating a Queue
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(createQueueRequest);
        } catch (AmazonSQSException exception) {
            if (!exception.getErrorCode().equals("QueueAlreadyExists")) {
                throw exception;
            }
        }


        // Getting the URL for a Queue
        String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

        // Deleting a Queue
        sqs.deleteQueue(queueUrl);

        sqs.createQueue("Queue1" + new Date().getTime());
        sqs.createQueue("Queue2" + new Date().getTime());
        sqs.createQueue("MyQueue" + new Date().getTime());

        // Listing Your Queues
        ListQueuesResult listQueuesResult = sqs.listQueues();
        System.out.println("List of all queue URLs in your account:");
        for (String url : listQueuesResult.getQueueUrls()) {
            System.out.println(url);
        }

        // Listing queues with filters
        String queueNamePrefix = "Queue";
        listQueuesResult = sqs.listQueues(new ListQueuesRequest(queueNamePrefix));
        System.out.println("Queue URLs that start with prefix: " + queueNamePrefix);
        for (String url : listQueuesResult.getQueueUrls()) {
            System.out.println(url);
        }
    }
}
