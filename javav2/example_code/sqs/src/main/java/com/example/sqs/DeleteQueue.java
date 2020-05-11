//snippet-sourcedescription:[DeleteQueue.java demonstrates how to delete an Amazon Simple Queue Service queue.]
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

// snippet-start:[sqs.java2.long_polling.complete]
package com.example.sqs;

// snippet-start:[sqs.java2.sqs_example.delete_queue.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
// snippet-end:[sqs.java2.sqs_example.delete_queue.import]
public class DeleteQueue {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "DeleteQueue - delete a queue\n\n" +
                "Usage: DeleteQueue <queueName>\n\n" +
                "Where:\n" +
                "  queueName - the name of the queue.\n\n" ;

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String queueName = args[0];

        SqsClient sqs = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        deleteSQSQueue(sqs, queueName);
    }

    // snippet-start:[sqs.java2.sqs_example.delete_queue]
    public static void deleteSQSQueue(SqsClient sqsClient, String queueName) {

        try {

            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                    .queueName(queueName)
                    .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

            DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .build();

            sqsClient.deleteQueue(deleteQueueRequest);

        } catch (QueueNameExistsException e) {
            e.printStackTrace();
            System.exit(1);
        }
        // snippet-end:[sqs.java2.sqs_example.delete_queue]
    }
}
// snippet-end:[sqs.java2.long_polling.complete]
