 
//snippet-sourcedescription:[LongPolling.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
 * Copyright 2011-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import java.util.HashMap;

import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

public class LongPolling
{
    public static void main(String[] args)
    {
        final String USAGE =
           "To run this example, supply the name of a queue to create and\n" +
           "queue url of an existing queue.\n\n" +
           "Ex: LongPolling <unique-queue-name> <existing-queue-url>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String queue_name = args[0];
        String queue_url = args[1];

        SqsClient sqs = SqsClient.builder().build();

        // Enable long polling when creating a queue
        HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
        attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
        
        CreateQueueRequest create_request = CreateQueueRequest.builder()
                .queueName(queue_name)
                .attributes(attributes)
                .build();

        try {
            sqs.createQueue(create_request);
        } catch (QueueNameExistsException e) {
        	throw e;
        }

        // Enable long polling on an existing queue
        SetQueueAttributesRequest set_attrs_request = SetQueueAttributesRequest.builder()
                .queueUrl(queue_url)
                .attributes(attributes)
                .build();
        sqs.setQueueAttributes(set_attrs_request);

        // Enable long polling on a message receipt
        ReceiveMessageRequest receive_request = ReceiveMessageRequest.builder()
                .queueUrl(queue_url)
                .waitTimeSeconds(20)
                .build();
        sqs.receiveMessage(receive_request);
    }
}

