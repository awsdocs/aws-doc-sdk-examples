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

// snippet-sourcedescription:[pinpoint_send_email_message_api demonstrates how to send a transactional email by using the SendMessages operation in the Amazon Pinpoint API.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-keyword:[Amazon Pinpoint]
// snippet-keyword:[Code Sample]
// snippet-keyword:[SendMessages]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_send_email_message_api.complete]

package com.amazonaws.samples;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.AmazonPinpointClientBuilder;
import com.amazonaws.services.pinpoint.model.AddressConfiguration;
import com.amazonaws.services.pinpoint.model.ChannelType;
import com.amazonaws.services.pinpoint.model.DirectMessageConfiguration;
import com.amazonaws.services.pinpoint.model.EmailMessage;
import com.amazonaws.services.pinpoint.model.MessageRequest;
import com.amazonaws.services.pinpoint.model.SendMessagesRequest;
import com.amazonaws.services.pinpoint.model.SimpleEmail;
import com.amazonaws.services.pinpoint.model.SimpleEmailPart;

public class SendMessages {

    // The AWS Region that you want to use to send the message. For a list of
    // AWS Regions where the Amazon Pinpoint API is available, see
    // https://docs.aws.amazon.com/pinpoint/latest/apireference/
    public static String region = "us-west-2";
    
    // The "From" address. This address has to be verified in Amazon 
    // Pinpoint in the region you're using to send email.
    public static String senderAddress = "sender@example.com";

    // The address on the "To" line. If your Amazon Pinpoint account is in
    // the sandbox, this address also has to be verified. 
    public static String toAddress = "recipient@example.com";

    // The Amazon Pinpoint project/application ID to use when you send this message.
    // Make sure that the SMS channel is enabled for the project or application
    // that you choose.
    public static String appId = "ce796be37f32f178af652b26eexample";

    // The subject line of the email.
    public static String subject = "Amazon Pinpoint test";

    // The email body for recipients with non-HTML email clients.
    static final String textBody = "Amazon Pinpoint Test (SDK for Java 2.x)\r\n"
            + "---------------------------------\r\n"
            + "This email was sent using the Amazon Pinpoint "
            + "API using the AWS SDK for Java 2.x.";
    
    // The body of the email for recipients whose email clients support
    // HTML content.
    static final String htmlBody = "<h1>Amazon Pinpoint test (AWS SDK for Java 2.x)</h1>"
            + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
            + "Amazon Pinpoint</a> Email API using the "
            + "<a href='https://aws.amazon.com/sdk-for-java/'>AWS SDK for Java 2.x</a>";

    // The character encoding the you want to use for the subject line and
    // message body of the email.
    public static String charset = "UTF-8";
     
    public static void main(String[] args) throws IOException {
          
        try {               
            Map<String,AddressConfiguration> addressMap = 
                new HashMap<String,AddressConfiguration>();
               
            addressMap.put(senderAddress, new AddressConfiguration()
                .withChannelType(ChannelType.EMAIL));
               
            AmazonPinpoint client = AmazonPinpointClientBuilder.standard()
                .withRegion(region).build();
               
            SendMessagesRequest request = (new SendMessagesRequest()
                .withApplicationId(appId)
                .withMessageRequest(new MessageRequest()
                    .withAddresses(addressMap)           
                    .withMessageConfiguration(new DirectMessageConfiguration()
                        .withEmailMessage(new EmailMessage()
                            .withSimpleEmail(new SimpleEmail()
                                .withHtmlPart(new SimpleEmailPart()
                                    .withCharset(charset)
                                    .withData(htmlBody)
                                )
                                .withTextPart(new SimpleEmailPart()
                                    .withCharset(charset)
                                    .withData(textBody)
                                )
                                .withSubject(new SimpleEmailPart()
                                    .withCharset(charset)
                                    .withData(subject)
                                )
                            )
                        )
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

// snippet-end:[pinpoint.java.pinpoint_send_email_message_api.complete]
