/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sqs;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SendReceiveMessages {

    private final String QUEUE_NAME = "Message.fifo";

    private SqsClient getClient() {
        return SqsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
    }

    public void purgeMyQueue() {
        SqsClient sqsClient = getClient();
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
            .queueName(QUEUE_NAME)
            .build();

        PurgeQueueRequest queueRequest = PurgeQueueRequest.builder()
             .queueUrl(sqsClient.getQueueUrl(getQueueRequest).queueUrl())
             .build();

        sqsClient.purgeQueue(queueRequest);
    }

    public List<MessageData> getMessages() {
        List attr = new ArrayList<String>();
        attr.add("Name");

        SqsClient sqsClient = getClient();
        try {
            GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build();

            String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(20)
                .messageAttributeNames(attr)
                .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();
            MessageData myMessage;
            List<MessageData> allMessages = new ArrayList<>();

            // Push the messages to a list.
            for (Message m : messages) {
                myMessage=new MessageData();
                myMessage.setBody(m.body());
                myMessage.setId(m.messageId());

                Map<String, MessageAttributeValue> map = m.messageAttributes();
                MessageAttributeValue val= map.get("Name");
                myMessage.setName(val.stringValue());
                allMessages.add(myMessage);
            }

            return allMessages;

        } catch (SqsException e) {
            e.getStackTrace();
        }
        return null;
    }

    public void processMessage(MessageData msg) {
    SqsClient sqsClient = getClient();

    try {
        MessageAttributeValue attributeValue = MessageAttributeValue.builder()
            .stringValue(msg.getName())
            .dataType("String")
            .build();

        Map myMap = new HashMap<String, MessageAttributeValue>();
        myMap.put("Name", attributeValue);
        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
            .queueName(QUEUE_NAME)
            .build();

        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();
        SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
            .queueUrl(queueUrl)
            .messageAttributes(myMap)
            .messageGroupId("GroupA")
            .messageDeduplicationId(msg.getId())
            .messageBody(msg.getBody())
            .build();
            sqsClient.sendMessage(sendMsgRequest);

        } catch (SqsException e) {
            e.getStackTrace();
        }
    }
}
