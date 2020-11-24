//snippet-sourcedescription:[SendMessages.java demonstrates how to send messages to an Amazon Simple Queue Service (Amazon SQS) queue.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Simple Queue Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
// snippet-start:[sqs.java2.send_recieve_messages.import]
package com.example.sqs;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
// snippet-end:[sqs.java2.send_recieve_messages.import]


public class SendMessages {


    public static void main(String[] args) {

        final String USAGE = "\n" +
                "SendMessages - send a message\n\n" +
                "Usage: SendMessages <queueName> <message>\n\n" +
                "Where:\n" +
                "  queueName - the name of the queue.\n\n" +
                "  message - the message to send.\n\n";

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
        sqsClient.close();
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

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sqs.java2.send_recieve_messages.main]
