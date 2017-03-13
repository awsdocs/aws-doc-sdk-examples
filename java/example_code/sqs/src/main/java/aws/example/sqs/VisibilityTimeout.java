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
package aws.example.sqs;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class VisibilityTimeout
{
    // Change the visibility timeout for a single message
    public ChangeMessageVisibilitySingle(String queue_url, timeout)
    {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Get the receipt handle for the first message in the queue.
        String receipt = sqs.receiveMessage(queue_url)
                            .getMessages()
                            .get(0)
                            .getReceiptHandle();

        sqs.changeMessageVisibility(queue_url, receipt, timeout);
    }

    // Change the visibility timeout for multiple messages.
    public ChangMessageVisibilityMultiple(String queue_url, timeout)
    {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        List<ChangeMessageVisibilityBatchRequestEntry> entries =
            new ArrayList<ChangeMessageVisibilityBatchRequestEntry>();

        entries.add(new ChangeMessageVisibilityBatchRequestEntry(
                    "unique_id_msg1",
                    sqs.receiveMessage(queue_url)
                       .getMessages()
                       .get(0)
                       .getReceiptHandle())
                .withVisibilityTimeout(timeout));

        entries.add(new ChangeMessageVisibilityBatchRequestEntry(
                    "unique_id_msg2",
                    sqs.receiveMessage(queue_url)
                       .getMessages()
                       .get(0)
                       .getReceiptHandle())
                .withVisibilityTimeout(timeout + 200));

        sqs.changeMessageVisibilityBatch(queue_url, entries);
    }

    public static void main(String[] args)
    {
        private static final String queue_name = "testQueue" +
            new Date().getTime();

        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // first, create a queue (unless it exists already)
        try {
            CreateQueueResult cq_result = sqs.createQueue(queue_name);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        String queue_url = sqs.getQueueUrl(queue_name).getQueueUrl();

        // Send some messages to the queue
        for (int i = 0; i < 20; i++) {
            sqs.sendMessage(queue_url, "This is message " + i);
        }

        // change visibility timeout (single)
        ChangeMessageVisibilitySingle(queue_url, 3600)

        // change visibility timeout (multiple)
        ChangeMessageVisibilityMultiple(queue_url, 2000)
    }
}

