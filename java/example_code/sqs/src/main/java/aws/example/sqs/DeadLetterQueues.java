//snippet-sourcedescription:[<<FILENAME>> demonstrates how to create and use a dead letter queue.]
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
package aws.example.sqs;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;

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

        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Create source queue
        try {
            sqs.createQueue(src_queue_name);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        // Create dead-letter queue
        try {
            sqs.createQueue(dl_queue_name);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        // Get dead-letter queue ARN
        String dl_queue_url = sqs.getQueueUrl(dl_queue_name)
                                 .getQueueUrl();

        GetQueueAttributesResult queue_attrs = sqs.getQueueAttributes(
                new GetQueueAttributesRequest(dl_queue_url)
                    .withAttributeNames("QueueArn"));

        String dl_queue_arn = queue_attrs.getAttributes().get("QueueArn");

        // Set dead letter queue with redrive policy on source queue.
        String src_queue_url = sqs.getQueueUrl(src_queue_name)
                                  .getQueueUrl();

        SetQueueAttributesRequest request = new SetQueueAttributesRequest()
                .withQueueUrl(src_queue_url)
                .addAttributesEntry("RedrivePolicy",
                        "{\"maxReceiveCount\":\"5\", \"deadLetterTargetArn\":\""
                        + dl_queue_arn + "\"}");

        sqs.setQueueAttributes(request);
    }
}
