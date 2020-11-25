//snippet-sourcedescription:[LongPolling.java demonstrates how to enable long polling on an Amazon Simple Queue Service (Amazon SQS) queue.]
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

// snippet-start:[sqs.java2.long_polling.complete]
package com.example.sqs;

// snippet-start:[sqs.java2.long_polling.import]
import java.util.Date;
import java.util.HashMap;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
// snippet-end:[sqs.java2.long_polling.import]

/*
 While the regular short polling returns immediately,
 long polling doesn't return a response until a message arrives
 in the message queue, or the long poll times out.
 */
public class LongPolling {

    private static final String QueueName = "testQueue" + new Date().getTime();

    public static void main(String[] args) {

        // Create a SqsClient object
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        setLongPoll(sqsClient) ;
        sqsClient.close();
    }

    // snippet-start:[sqs.java2.long_polling.main]
    public static void setLongPoll( SqsClient sqsClient) {

        // Enable long polling when creating a queue
        HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
        attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");

        CreateQueueRequest createRequest = CreateQueueRequest.builder()
                .queueName(QueueName)
                .attributes(attributes)
                .build();

        try {
            sqsClient.createQueue(createRequest);

            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(QueueName)
                .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

           // Enable long polling on an existing queue
           SetQueueAttributesRequest setAttrsRequest = SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributes(attributes)
                .build();

           sqsClient.setQueueAttributes(setAttrsRequest);

            // Enable long polling on a message receipt
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .waitTimeSeconds(20)
                .build();

            sqsClient.receiveMessage(receiveRequest);

        } catch (SqsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sqs.java2.long_polling.main]
// snippet-end:[sqs.java2.long_polling.complete]