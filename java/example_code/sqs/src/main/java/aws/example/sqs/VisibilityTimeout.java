// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class VisibilityTimeout {
    // Change the visibility timeout for a single message
    public static void changeMessageVisibilitySingle(
            String queue_url, int timeout) {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Get the receipt handle for the first message in the queue.
        String receipt = sqs.receiveMessage(queue_url)
                .getMessages()
                .get(0)
                .getReceiptHandle();

        sqs.changeMessageVisibility(queue_url, receipt, timeout);
    }

    // Change the visibility timeout for multiple messages.
    public static void changeMessageVisibilityMultiple(
            String queue_url, int timeout) {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        List<ChangeMessageVisibilityBatchRequestEntry> entries = new ArrayList<ChangeMessageVisibilityBatchRequestEntry>();

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

    public static void main(String[] args) {
        final String queue_name = "testQueue" + new Date().getTime();
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // first, create a queue (unless it exists already)
        try {
            CreateQueueResult cq_result = sqs.createQueue(queue_name);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        final String queue_url = sqs.getQueueUrl(queue_name).getQueueUrl();

        // Send some messages to the queue
        for (int i = 0; i < 20; i++) {
            sqs.sendMessage(queue_url, "This is message " + i);
        }

        // change visibility timeout (single)
        changeMessageVisibilitySingle(queue_url, 60 * 60); // 1 hour

        // change visibility timeout (multiple)
        changeMessageVisibilityMultiple(queue_url, 30 * 60); // 30 minutes
    }
}
