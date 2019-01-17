//snippet-sourcedescription:[DeadLetterQueues.java demonstrates how to set a queue as a dead letter queue.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.QueueNameExistsException;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesResponse;

import java.util.HashMap;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class DeadLetterQueues
{
    public static void main(String[] args)
    {
        if (args.length != 2) {
            System.out.println(
                "Usage: DeadLetterQueues <src_queue_name> <dl_queue_name>");
            System.exit(1);
        }

        String src_queue_name = args[0];
        String dl_queue_name = args[1];

        SqsClient sqs = SqsClient.builder().region(Region.US_WEST_2).build();

        CreateQueueRequest request = CreateQueueRequest.builder()
        		.queueName(src_queue_name).build();

        // Create source queue
        try {
            sqs.createQueue(request);
        } catch (QueueNameExistsException e) {
        	throw e;

        }

        CreateQueueRequest dlrequest = CreateQueueRequest.builder()
        		.queueName(dl_queue_name).build();

        // Create dead-letter queue
        try {
            sqs.createQueue(dlrequest);
        } catch (QueueNameExistsException e) {
        	throw e;

        }


        GetQueueUrlRequest getRequest = GetQueueUrlRequest.builder()
        		.queueName(dl_queue_name)
        		.build();

        // Get dead-letter queue ARN
        String dl_queue_url = sqs.getQueueUrl(getRequest)
                                 .queueUrl();

        GetQueueAttributesResponse queue_attrs = sqs.getQueueAttributes(
                GetQueueAttributesRequest.builder()
                .queueUrl(dl_queue_url)
                .attributeNames(QueueAttributeName.QUEUE_ARN).build());

        String dl_queue_arn = queue_attrs.attributes().get(QueueAttributeName.QUEUE_ARN);

        // Set dead letter queue with redrive policy on source queue.
        GetQueueUrlRequest getRequestSource = GetQueueUrlRequest.builder()
        		.queueName(src_queue_name)
        		.build();

        String src_queue_url = sqs.getQueueUrl(getRequestSource)
                                  .queueUrl();

        HashMap<QueueAttributeName, String> attributes = new HashMap<QueueAttributeName, String>();
        attributes.put(QueueAttributeName.REDRIVE_POLICY, "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\""
                + dl_queue_arn + "\"}");

        SetQueueAttributesRequest setAttrRequest = SetQueueAttributesRequest.builder()
                .queueUrl(src_queue_url)
                .attributes(attributes)
                .build();

        SetQueueAttributesResponse setAttrResponse = sqs.setQueueAttributes(setAttrRequest);
    }
}
