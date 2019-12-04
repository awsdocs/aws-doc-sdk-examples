//snippet-sourcedescription:[UsingQueues.java demonstrates how to create, and delete a queue as well as list all queues in your account.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Simple Queue Service]
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
package aws.example.sqs;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import java.util.Date;

public class UsingQueues
{
    private static final String QUEUE_NAME = "testQueue" +
            new Date().getTime();

    public static void main(String[] args)
    {
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        // Creating a Queue
        CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
                .addAttributesEntry("DelaySeconds", "60")
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {
            sqs.createQueue(create_request);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        // Get the URL for a queue
        String queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

        // Delete the Queue
        sqs.deleteQueue(queue_url);

        sqs.createQueue("Queue1" + new Date().getTime());
        sqs.createQueue("Queue2" + new Date().getTime());
        sqs.createQueue("MyQueue" + new Date().getTime());

        // List your queues
        ListQueuesResult lq_result = sqs.listQueues();
        System.out.println("Your SQS Queue URLs:");
        for (String url : lq_result.getQueueUrls()) {
            System.out.println(url);
        }

        // List queues with filters
        String name_prefix = "Queue";
        lq_result = sqs.listQueues(new ListQueuesRequest(name_prefix));
        System.out.println("Queue URLs with prefix: " + name_prefix);
        for (String url : lq_result.getQueueUrls()) {
            System.out.println(url);
        }

        //Shutdown the client object
        sqs.shutdown();
    }
}
