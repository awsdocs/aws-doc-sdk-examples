/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
*/

// snippet-sourcedescription:[pinpoint_send_sms_message_api demonstrates how send a transactional SMS message by using the SendMessages operation in the Amazon Pinpoint API.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[SendMessages]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_send_sms_message_api.complete]

package com.amazonaws.samples;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.AddressConfiguration;
import com.amazonaws.services.pinpoint.model.ChannelType;
import com.amazonaws.services.pinpoint.model.DirectMessageConfiguration;
import com.amazonaws.services.pinpoint.model.MessageRequest;
import com.amazonaws.services.pinpoint.model.SMSMessage;
import com.amazonaws.services.pinpoint.model.SendMessagesRequest;

public class SendMessage {

    // The AWS Region that you want to use to send the message. For a list of
    // AWS Regions where the Amazon Pinpoint API is available, see
    // https://docs.aws.amazon.com/pinpoint/latest/apireference/
    public static String region = "us-east-1";
     
    // The phone number or short code to send the message from. The phone number 
    // or short code that you specify has to be associated with your Amazon Pinpoint 
    // account. For best results, specify long codes in E.164 format.
    public static String originationNumber = "+12065550199";
     
    // The recipient's phone number.  For best results, you should specify the
    // phone number in E.164 format.
    public static String destinationNumber = "+14255550142";
     
    // The content of the SMS message.     
    public static String message = "This message was sent through Amazon Pinpoint "
            + "using the AWS SDK for Java. Reply STOP to "
            + "opt out.";
     
    // The Pinpoint project/application ID to use when you send this message.
    // Make sure that the SMS channel is enabled for the project or application
    // that you choose.
    public static String appId = "ce796be37f32f178af652b26eexample";

    // The type of SMS message that you want to send. If you plan to send 
    // time-sensitive content, specify TRANSACTIONAL. If you plan to send 
    // marketing-related content, specify PROMOTIONAL.
    public static String messageType = "TRANSACTIONAL";
    
    // The registered keyword associated with the originating short code.
    public static String registeredKeyword = "myKeyword";
     
    // The sender ID to use when sending the message. Support for sender ID 
    // varies by country or region. For more information, see
    // https://docs.aws.amazon.com/pinpoint/latest/userguide/channels-sms-countries.html
    public static String senderId = "MySenderID";
     
    public static void main(String[] args) throws IOException {
          
        try {               
            Map<String,AddressConfiguration> addressMap = 
                    new HashMap<String,AddressConfiguration>();
               
            addressMap.put(destinationNumber, new AddressConfiguration()
                    .withChannelType(ChannelType.SMS));
               
            AmazonPinpoint client = AmazonPinpointClientBuilder.standard()
                    .withRegion(region).build();
               
            SendMessagesRequest request = new SendMessagesRequest()
                    .withApplicationId(appId)
                    .withMessageRequest(new MessageRequest()
                    .withAddresses(addressMap)                                   
                    .withMessageConfiguration(new DirectMessageConfiguration()
                            .withSMSMessage(new SMSMessage()
                                    .withBody(message)
                                    .withMessageType(messageType)
                                    .withOriginationNumber(originationNumber)
                                    .withSenderId(senderId)
                                    .withKeyword(registeredKeyword)
                            )
                    )
            );
            System.out.println("Sending message...");               
            client.sendMessages(request);
            System.out.println("Message sent!");
    } catch (Exception ex) {
        System.out.println("The message wasn't sent. Error message: " 
                + ex.getMessage());
        }
    }
}

// snippet-end:[pinpoint.java.pinpoint_send_sms_message_api.complete]
