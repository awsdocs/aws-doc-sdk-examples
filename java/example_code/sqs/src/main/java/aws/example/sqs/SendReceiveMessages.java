 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
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
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import java.util.Date;
import java.util.List;

public class SendReceiveMessages
{
    private static final String QUEUE_NAME = "testQueue" + new Date().getTime();

    public static void main(String[] args)
    {
        final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        try {
            CreateQueueResult create_result = sqs.createQueue(QUEUE_NAME);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
                throw e;
            }
        }

        String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody("hello world")
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);


        // Send multiple messages to the queue
        SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(
                        new SendMessageBatchRequestEntry(
                                "msg_1", "Hello from message 1"),
                        new SendMessageBatchRequestEntry(
                                "msg_2", "Hello from message 2")
                                .withDelaySeconds(10));
        sqs.sendMessageBatch(send_batch_request);

        // receive messages from the queue
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();

        // delete messages from the queue
        for (Message m : messages) {
            sqs.deleteMessage(queueUrl, m.getReceiptHandle());
        }
    }
}

