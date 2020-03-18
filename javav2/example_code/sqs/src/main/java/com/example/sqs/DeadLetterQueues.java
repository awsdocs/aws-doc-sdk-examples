//snippet-sourcedescription:[DeadLetterQueues.java demonstrates how to set a queue as a dead letter queue.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2020-02-20]
//snippet-sourceauthor:[scmacdon-aws]
// snippet-start:[sqs.java2.delete_letter_queues.complete]
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
// snippet-start:[sqs.java2.delete_letter_queues.import]
package com.example.sqs;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesResponse;

import java.util.Date;
import java.util.HashMap;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

// snippet-end:[sqs.java2.delete_letter_queues.import]
// snippet-start:[sqs.java2.delete_letter_queues.main]
public class DeadLetterQueues {
    private static final String QueueName = "testQueue" + new Date().getTime();
    private static final String DLQueueName = "DLQueue" + new Date().getTime();

    public static void main(String[] args) {
        SqsClient sqs = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try {
            CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName(QueueName).build();

             CreateQueueRequest dlrequest = CreateQueueRequest.builder()
                .queueName(DLQueueName).build();

            sqs.createQueue(dlrequest);

            GetQueueUrlRequest getRequest = GetQueueUrlRequest.builder()
                .queueName(DLQueueName)
                .build();

            // Get dead-letter queue ARN
            String dlQueueUrl = sqs.getQueueUrl(getRequest)
                .queueUrl();

            GetQueueAttributesResponse queueAttrs = sqs.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                        .queueUrl(dlQueueUrl)
                        .attributeNames(QueueAttributeName.QUEUE_ARN).build());

            String dlQueueArn = queueAttrs.attributes().get(QueueAttributeName.QUEUE_ARN);

            // Set dead letter queue with redrive policy on source queue.
            GetQueueUrlRequest getRequestSource = GetQueueUrlRequest.builder()
                .queueName(DLQueueName)
                .build();

            String srcQueueUrl = sqs.getQueueUrl(getRequestSource)
                .queueUrl();

            HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
            attributes.put(QueueAttributeName.REDRIVE_POLICY, "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\""
                + dlQueueArn + "\"}");

            SetQueueAttributesRequest setAttrRequest = SetQueueAttributesRequest.builder()
                .queueUrl(srcQueueUrl)
                .attributes(attributes)
                .build();

            SetQueueAttributesResponse setAttrResponse = sqs.setQueueAttributes(setAttrRequest);

        } catch (QueueNameExistsException e) {
            throw e;
        }
    }
}
// snippet-end:[sqs.java2.delete_letter_queues.main]
// snippet-end:[sqs.java2.delete_letter_queues.complete]
