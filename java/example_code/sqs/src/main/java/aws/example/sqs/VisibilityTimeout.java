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
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import com.amazonaws.services.sqs.model.CreateQueueResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisibilityTimeout {

    private static final String QUEUE_NAME = "testQueue" + new Date().getTime();

    public static void main(String[] args) {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        try {
            CreateQueueResult createQueueResult = sqs.createQueue(QUEUE_NAME);
        } catch (AmazonSQSException exception) {
            if (!exception.getErrorCode().equals("QueueAlreadyExists")) {
                throw exception;
            }
        }

        String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

        // Sending messages to queue to use while changing visibility timeout
        for (int i = 0; i < 20; i++) {
            sqs.sendMessage(queueUrl, "This is message " + i);
        }

        /* Changing the Visibility Timeout for single message */

        // Get the receipt handle for the first message in the queue.
        String receiptHandle = sqs.receiveMessage(queueUrl).getMessages().get(0).getReceiptHandle();
        // change the visibility timeout of that message to 1 hour
        sqs.changeMessageVisibility(queueUrl, receiptHandle, 3600);



        // Changing the Visibility Timeout for multiple messages.
        List<ChangeMessageVisibilityBatchRequestEntry> entries = new ArrayList<ChangeMessageVisibilityBatchRequestEntry>();
        entries.add(new ChangeMessageVisibilityBatchRequestEntry("unique_id_msg1", sqs.receiveMessage(queueUrl).getMessages().get(0).getReceiptHandle())
                .withVisibilityTimeout(1000));
        entries.add(new ChangeMessageVisibilityBatchRequestEntry("unique_id_msg2", sqs.receiveMessage(queueUrl).getMessages().get(0).getReceiptHandle())
                .withVisibilityTimeout(2000));
        sqs.changeMessageVisibilityBatch(queueUrl, entries);
    }
}

