/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.example;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;

import java.io.StringWriter;
import java.util.*;

@Component
public class SendReceiveMessages {

    private final String QUEUE_NAME = "Message.fifo";

 private SqsClient getClient() {
     SqsClient sqsClient = SqsClient.builder()
             .region(Region.US_WEST_2)
             .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
             .build();

        return sqsClient;
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

    public String getMessages() {

        List attr = new ArrayList<String>();
        attr.add("Name");

        SqsClient sqsClient = getClient();

        try {

        GetQueueUrlRequest getQueueRequest = GetQueueUrlRequest.builder()
                .queueName(QUEUE_NAME)
                .build();

        String queueUrl = sqsClient.getQueueUrl(getQueueRequest).queueUrl();

        // Receive messages from the queue
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .messageAttributeNames(attr)
                .build();
        List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

        com.example.Message myMessage;

        List allMessages = new ArrayList<com.example.Message>();

        // Push the messages to a list
        for (Message m : messages) {

            myMessage=new com.example.Message();
            myMessage.setBody(m.body());

            Map map = m.messageAttributes();
            MessageAttributeValue val=(MessageAttributeValue)map.get("Name");
            myMessage.setName(val.stringValue());

            allMessages.add(myMessage);
        }

        return convertToString(toXml(allMessages));

    } catch (SqsException e) {
        e.getStackTrace();
    }
        return "";
}

    public void processMessage(com.example.Message msg) {

        SqsClient sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        try {

            // Get user
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

            // generate the work item ID
            UUID uuid = UUID.randomUUID();
            String msgId1 = uuid.toString();

            SendMessageRequest sendMsgRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageAttributes(myMap)
                    .messageGroupId("GroupA")
                    .messageDeduplicationId(msgId1)
                    .messageBody(msg.getBody())
                    .build();
            sqsClient.sendMessage(sendMsgRequest);


        } catch (SqsException e) {
             e.getStackTrace();
        }

    }

    // Convert item data retrieved from the Message Queue
    // into XML to pass back to the view
    private Document toXml(List<com.example.Message> itemList) {

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            // Start building the XML
            Element root = doc.createElement( "Messages" );
            doc.appendChild( root );

            // Get the elements from the collection
            int custCount = itemList.size();

            // Iterate through the collection
            for ( int index=0; index < custCount; index++) {

                // Get the WorkItem object from the collection
                com.example.Message myMessage = itemList.get(index);

                Element item = doc.createElement( "Message" );
                root.appendChild( item );

                // Set Id
                Element id = doc.createElement( "Data" );
                id.appendChild( doc.createTextNode(myMessage.getBody()));
                item.appendChild( id );

                // Set Name
                Element name = doc.createElement( "User" );
                name.appendChild( doc.createTextNode(myMessage.getName() ) );
                item.appendChild( name );

            }

            return doc;
        } catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertToString(Document xml) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(xml);
            transformer.transform(source, result);
            return result.getWriter().toString();

        } catch(TransformerException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}