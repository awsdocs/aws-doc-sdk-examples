//snippet-sourcedescription:[LongPolling.java demonstrates how to enable long polling on a queue.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2/24/2020]
//snippet-sourceauthor:[scmacdon-aws]
// snippet-start:[sqs.java2.long_polling.complete]
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
// snippet-start:[sqs.java2.long_polling.import]
package com.example.sqs;
import java.util.Date;
import java.util.HashMap;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;

// snippet-end:[sqs.java2.long_polling.import]
// snippet-start:[sqs.java2.long_polling.main]
/*
 While the regular short polling returns immediately,
 long polling doesn't return a response until a message arrives
 in the message queue, or the long poll times out.
 */
public class LongPolling {

    private static final String QueueName = "testQueue" + new Date().getTime();

    public static void main(String[] args){

        // Create a SqsClient object
        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build() ;

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

        } catch (QueueNameExistsException e) {
            throw e;
        }
    }
}
// snippet-end:[sqs.java2.long_polling.main]
// snippet-end:[sqs.java2.long_polling.complete]
