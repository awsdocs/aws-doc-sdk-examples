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
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

public class LongPolling {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "To run this example, supply the name of a queue to create and queue url of an existing queue\n" +
                "Ex: LongPolling <unique-queue-name> <existing-queue-url>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String uniqueQueueName = args[0];
        String existingQueueUrl = args[1];


        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Enabling Long Polling When Creating a Queue
        CreateQueueRequest createQueueRequest = new CreateQueueRequest()
                                                        .withQueueName(uniqueQueueName)
                                                        .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");
        try {
            sqs.createQueue(createQueueRequest);
        } catch (AmazonSQSException exception) {
            if (!exception.getErrorCode().equals("QueueAlreadyExists")) {
                throw exception;
            }
        }

        //Enabling Long Polling on an Existing Queue
        SetQueueAttributesRequest setQueueAttributesRequest = new SetQueueAttributesRequest()
                .withQueueUrl(existingQueueUrl)
                .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");
        sqs.setQueueAttributes(setQueueAttributesRequest);



        // Enabling Long Polling on Message Receipt
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(existingQueueUrl)
                .withWaitTimeSeconds(20);
        sqs.receiveMessage(receiveMessageRequest);
    }
}
