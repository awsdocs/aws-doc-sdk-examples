/*
 * Copyright 2011-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import java.util.Date;

public class DeadLetterQueues
{
    private static final String DEAD_LETTER_QUEUE_NAME = "DLQueue" +
        new Date().getTime();
    private static final String SOURCE_QUEUE_NAME = "SrcQueue" +
        new Date().getTime();

    public static void main(String[] args)
    {
        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Create deadLetter queue
        try {
            sqs.createQueue(DEAD_LETTER_QUEUE_NAME);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        String dl_queue_url = sqs.getQueueUrl(DEAD_LETTER_QUEUE_NAME)
                                 .getQueueUrl();

        System.out.println("Dead letter queue URL: " + dl_queue_url);

        GetQueueAttributesResult queue_attrs = sqs.getQueueAttributes(
                new GetQueueAttributesRequest(dl_queue_url)
                    .withAttributeNames("QueueArn"));

        String dl_queue_arn = queue_attrs.getAttributes().get("QueueArn");

        // Create source queue
        try {
            sqs.createQueue(SOURCE_QUEUE_NAME).getQueueUrl();
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        String src_queue_url = sqs.getQueueUrl(SOURCE_QUEUE_NAME)
                                  .getQueueUrl();

        System.out.println("Source queue URL: " + src_queue_url);

        // Configuring source queue
        SetQueueAttributesRequest sqa_request = new SetQueueAttributesRequest()
                .withQueueUrl(src_queue_url)
                .addAttributesEntry("RedrivePolicy",
                        "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\""
                        + dl_queue_arn + "\"}");
        sqs.setQueueAttributes(sqa_request);
    }
}

