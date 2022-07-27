//snippet-sourcedescription:[VisibilityTimeout.java demonstrates how to change the visibility timeout for messages in an Amazon Simple Queue Service (Amazon SQS) queue.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Simple Queue Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.sqs;

// snippet-start:[sqs.java2.visibility_timeout.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchRequest;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
// snippet-end:[sqs.java2.visibility_timeout.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class VisibilityTimeout {

    // snippet-start:[sqs.java2.visibility_timeout.main]
    public static void main(String[] args) {
        final String queueName = "testQueue" + new Date().getTime();
        SqsClient sqs = SqsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        // First, create a queue (unless it exists already)
        CreateQueueRequest createRequest = CreateQueueRequest.builder()
            .queueName(queueName)
            .build();
        try {
            sqs.createQueue(createRequest);
        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        // Send some messages to the queue
        for (int i = 0; i < 20; i++) {
            SendMessageRequest sendRequest = SendMessageRequest.builder()
                .queueUrl(queueName)
                .messageBody("This is message " + i)
                .build();
            sqs.sendMessage(sendRequest);
        }

        // change visibility timeout (single)
        changeMessageVisibilitySingle(sqs, queueName, 3600);

        // change visibility timeout (multiple)
        changeMessageVisibilityMultiple(sqs, queueName, 2000);
        sqs.close();
    }

    // Change the visibility timeout for a single message
    public static void changeMessageVisibilitySingle(SqsClient sqs, String queueName, int timeout) {

        try {
            // Get the receipt handle for the first message in the queue.
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueName)
                .build();
            String receipt = sqs.receiveMessage(receiveRequest)
                .messages()
                .get(0)
                .receiptHandle();

            ChangeMessageVisibilityRequest visibilityRequest = ChangeMessageVisibilityRequest.builder()
                .queueUrl(queueName)
                .receiptHandle(receipt)
                .visibilityTimeout(timeout)
                .build();
            sqs.changeMessageVisibility(visibilityRequest);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    // Change the visibility timeout for multiple messages.
    public static void changeMessageVisibilityMultiple(SqsClient sqs, String queue_url, int timeout) {

        try {
            List<ChangeMessageVisibilityBatchRequestEntry> entries = new ArrayList<>();
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queue_url)
                .build();

            String receipt = sqs.receiveMessage(receiveRequest)
                .messages()
                .get(0)
                .receiptHandle();

            entries.add(ChangeMessageVisibilityBatchRequestEntry.builder()
                .id("unique_id_msg1")
                .receiptHandle(receipt)
                .visibilityTimeout(timeout)
                .build());

            entries.add(ChangeMessageVisibilityBatchRequestEntry.builder()
                .id("unique_id_msg2")
                .receiptHandle(receipt)
                .visibilityTimeout(timeout + 200)
                .build());

            ChangeMessageVisibilityBatchRequest batchRequest = ChangeMessageVisibilityBatchRequest.builder()
                .queueUrl(queue_url)
                .entries(entries)
                .build();

            sqs.changeMessageVisibilityBatch(batchRequest);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[sqs.java2.visibility_timeout.main]
}

