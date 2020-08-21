//snippet-sourcedescription:[SendMessages.java demonstrates how to send messages to a queue.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/20/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[sqs.java2.send_recieve_messages.import]
package com.example.sqs;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
// snippet-end:[sqs.java2.send_recieve_messages.import]


public class SendMessages {


    public static void main(String[] args) {

        final String USAGE = "\n" +
                "SendMessages - send a message\n\n" +
                "Usage: SendMessages <queueName> <message>\n\n" +
                "Where:\n" +
                "  queueName - the name of the queue\n\n" +
                "  message - the message to send\n\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        String queueName = args[0];
        String message = args[1];

        sendMessage(sqsClient, queueName, message);
    }

    // snippet-start:[sqs.java2.send_recieve_messages.main]
    public static void sendMessage(SqsClient sqsClient, String queueName, String message) {

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                    .queueName(queueName)
                    .build();
            CreateQueueResponse createResult = sqsClient.createQueue(request);

            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .delaySeconds(5)
                .build();
            sqsClient.sendMessage(sendMsgRequest);

        } catch (QueueNameExistsException e) {
            throw e;
        }
    }
}
// snippet-end:[sqs.java2.send_recieve_messages.main]
