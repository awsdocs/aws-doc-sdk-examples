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

// snippet-sourcedescription:[pinpoint_send_email_message_email_api demonstrates how to send a transactional email message by using the Amazon Pinpoint Email API.]
// snippet-service:[Amazon Pinpoint]
// snippet-keyword:[Java]
// snippet-sourcesyntax:[java]
// snippet-keyword:[Amazon Pinpoint Email API]
// snippet-keyword:[Code Sample]
// snippet-keyword:[SendEmail]
// snippet-sourcetype:[snippet]
// snippet-sourcedate:[2019-01-20]
// snippet-sourceauthor:[AWS]
// snippet-start:[pinpoint.java.pinpoint_send_email_message_email_api.complete]

package com.amazonaws.samples;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.amazonaws.services.pinpointemail.AmazonPinpointEmail;
import com.amazonaws.services.pinpointemail.AmazonPinpointEmailClientBuilder;
import com.amazonaws.services.pinpointemail.model.Body;
import com.amazonaws.services.pinpointemail.model.Content;
import com.amazonaws.services.pinpointemail.model.Destination;
import com.amazonaws.services.pinpointemail.model.EmailContent;
import com.amazonaws.services.pinpointemail.model.Message;
import com.amazonaws.services.pinpointemail.model.MessageTag;
import com.amazonaws.services.pinpointemail.model.SendEmailRequest;

public class SendMessage {

    // The AWS Region that you want to use to send the email. For a list of
    // AWS Regions where the Amazon Pinpoint Email API is available, see 
    // https://docs.aws.amazon.com/pinpoint-email/latest/APIReference
    static final String region = "us-west-2";
    
    // The "From" address. This address has to be verified in Amazon 
    // Pinpoint in the region you're using to send email.
    static final String senderAddress = "sender@example.com";
    
    // The address on the "To" line. If your Amazon Pinpoint account is in
    // the sandbox, this address also has to be verified. 
    static final String toAddress = "recipient@example.com";
    
    // CC and BCC addresses. If your account is in the sandbox, these 
    // addresses have to be verified. 
    // In this example, there are several CC recipients. Each address is separated
    // with a comma. We convert this string to an ArrayList later in the example.
    static final String ccAddress = "cc_recipient0@example.com, cc_recipient1@example.com";
    static final String bccAddress = "bcc_recipient@example.com";
    
    // The configuration set that you want to use to send the email.
    static final String configurationSet = "ConfigSet";
    
    // The subject line of the email.
    static final String subject = "Amazon Pinpoint Email API test";
    
    // The email body for recipients with non-HTML email clients.
    static final String textBody = "Amazon Pinpoint Email Test (Java)\r\n"
            + "This email was sent using the Amazon Pinpoint "
            + "Email API using the AWS SDK for Java.";
    
    // The body of the email for recipients whose email clients support
    // HTML content.
    static final String htmlBody = "<h1>Amazon Pinpoint test (AWS SDK for Java)</h1>"
            + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
            + "Amazon Pinpoint</a> Email API using the "
            + "<a href='https://aws.amazon.com/sdk-for-java/'>AWS SDK for Java</a>";
        
    // The message tags that you want to apply to the email.
    static final String tagKey0 = "key0";
    static final String tagValue0 = "value0";
    static final String tagKey1 = "key1";
    static final String tagValue1 = "value1";
        
    // The character encoding the you want to use for the subject line and
    // message body of the email.
    static final String charset = "UTF-8";
    
    public static void main(String[] args) throws IOException {
    
        // Convert comma-separated lists of To, CC, and BCC Addresses into collections.
        // This works even if the string only contains a single email address.
        Collection<String> toAddresses = 
                new ArrayList<String>(Arrays.asList(toAddress.split("\\s*,\\s*")));
        Collection<String> ccAddresses = 
                new ArrayList<String>(Arrays.asList(ccAddress.split("\\s*,\\s*")));
        Collection<String> bccAddresses = 
                new ArrayList<String>(Arrays.asList(bccAddress.split("\\s*,\\s*")));
    
        try {
            // Create a new email client
            AmazonPinpointEmail client = AmazonPinpointEmailClientBuilder.standard()
                    .withRegion(region).build();
                    
            // Combine all of the components of the email to create a request.
            SendEmailRequest request = new SendEmailRequest()
                    .withFromEmailAddress(senderAddress)
                    .withConfigurationSetName(configurationSet)
                    .withDestination(new Destination()
                        .withToAddresses(toAddresses)
                        .withCcAddresses(ccAddresses)
                        .withBccAddresses(bccAddresses)
                    )
                    .withContent(new EmailContent()
                        .withSimple(new Message()
                            .withSubject(new Content()
                                .withCharset(charset)
                                .withData(subject)
                            )
                            .withBody(new Body()
                                .withHtml(new Content()
                                    .withCharset(charset)
                                    .withData(htmlBody)
                                )
                                .withText(new Content()
                                    .withCharset(charset)
                                    .withData(textBody)
                                )
                            )
                        )
                    )
                    .withEmailTags(new MessageTag()
                        .withName(tagKey0)
                        .withValue(tagValue0)
                    )
                    .withEmailTags(new MessageTag()
                        .withName(tagKey1)
                        .withValue(tagValue1)
                    );
            client.sendEmail(request);
            System.out.println("Email sent!");
            System.out.println(request);
        } catch (Exception ex) {
            System.out.println("The email wasn't sent. Error message: " 
                    + ex.getMessage());
        }
    }
}

// snippet-end:[pinpoint.java.pinpoint_send_email_message_email_api.complete]
