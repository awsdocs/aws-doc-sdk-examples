//snippet-sourcedescription:[LongPolling.java demonstrates how to enable long polling when creating a queue.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Queue Service]
//snippet-service:[sqs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-12-04]
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
package aws.example.sqs;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class LongPolling
{
    public static void main(String[] args)
    {
        final String USAGE =
                "To run this example, supply the name of a queue to create and\n" +
                        "queue url of an existing queue.\n\n" +
                        "Ex: LongPolling <unique-queue-name> <existing-queue-url>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String queue_name = args[0];
        String queue_url = args[1];

        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Enable long polling when creating a queue
        CreateQueueRequest create_request = new CreateQueueRequest()
                .withQueueName(queue_name)
                .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");

        try {
            sqs.createQueue(create_request);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        // Enable long polling on an existing queue
        SetQueueAttributesRequest set_attrs_request = new SetQueueAttributesRequest()
                .withQueueUrl(queue_url)
                .addAttributesEntry("ReceiveMessageWaitTimeSeconds", "20");
        sqs.setQueueAttributes(set_attrs_request);

        // Enable long polling on a message receipt
        ReceiveMessageRequest receive_request = new ReceiveMessageRequest()
                .withQueueUrl(queue_url)
                .withWaitTimeSeconds(40);
        sqs.receiveMessage(receive_request);
    }
}