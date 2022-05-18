//snippet-sourcedescription:[SendEmailMessage.java demonstrates how to send an email message.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Pinpoint]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pinpoint;

//snippet-start:[pinpoint.java2.send_email.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.AddressConfiguration;
import software.amazon.awssdk.services.pinpoint.model.ChannelType;
import software.amazon.awssdk.services.pinpoint.model.SimpleEmailPart;
import software.amazon.awssdk.services.pinpoint.model.SimpleEmail;
import software.amazon.awssdk.services.pinpoint.model.EmailMessage;
import software.amazon.awssdk.services.pinpoint.model.DirectMessageConfiguration;
import software.amazon.awssdk.services.pinpoint.model.MessageRequest;
import software.amazon.awssdk.services.pinpoint.model.SendMessagesRequest;
import software.amazon.awssdk.services.pinpoint.model.PinpointException;
import java.util.HashMap;
import java.util.Map;
//snippet-end:[pinpoint.java2.send_email.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class SendEmailMessage {

    // The email body for recipients with non-HTML email clients.
    static final String textBody = "Amazon Pinpoint Test (SDK for Java 2.x)\r\n"
            + "---------------------------------\r\n"
            + "This email was sent using the Amazon Pinpoint "
            + "API using the AWS SDK for Java version 2.";

    // The body of the email for recipients whose email clients support HTML content.
    static final String htmlBody = "<h1>Amazon Pinpoint test (AWS SDK for Java 2.x)</h1>"
            + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
            + "Amazon Pinpoint</a> Email API using the "
            + "<a href='https://aws.amazon.com/sdk-for-java/'>AWS SDK for Java 2.x</a>";

    // The character encoding the you want to use for the subject line and
    // message body of the email.
    public static String charset = "UTF-8";

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage: " +
                "   <subject> <appId> <senderAddress> <toAddress>\n\n" +
                "Where:\n" +
                "   subject - The email subject to use.\n\n"+
                "   appId - The Amazon Pinpoint project/application ID to use when you send this message\n\n" +
                "   senderAddress - The from address. This address has to be verified in Amazon Pinpoint in the region you're using to send email \n\n" +
                "   toAddress - The to address. This address has to be verified in Amazon Pinpoint in the region you're using to send email \n\n" ;

        if (args.length != 4) {
           System.out.println(usage);
           System.exit(1);
       }

        String subject = args[0];
        String appId = args[1] ;
        String senderAddress = args[2] ;
        String toAddress = args[3] ;

        System.out.println("Sending a message" );
        PinpointClient pinpoint = PinpointClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        sendEmail(pinpoint, subject, appId,  senderAddress, toAddress);
        System.out.println("Email was sent");
        pinpoint.close();
    }

    //snippet-start:[pinpoint.java2.send_email.main]
    public static void sendEmail(PinpointClient pinpoint,
                                 String subject,
                                 String appId,
                                 String senderAddress,
                                 String toAddress) {

        try {

            Map<String,AddressConfiguration> addressMap = new HashMap<String,AddressConfiguration>();
            AddressConfiguration configuration =  AddressConfiguration.builder()
                    .channelType(ChannelType.EMAIL)
                    .build();

            addressMap.put(toAddress, configuration);
            SimpleEmailPart emailPart = SimpleEmailPart.builder()
                    .data(htmlBody)
                    .charset(charset)
                    .build() ;

            SimpleEmailPart subjectPart = SimpleEmailPart.builder()
                    .data(subject)
                    .charset(charset)
                    .build() ;

            SimpleEmail simpleEmail = SimpleEmail.builder()
                    .htmlPart(emailPart)
                    .subject(subjectPart)
                    .build();

            EmailMessage emailMessage =  EmailMessage.builder()
                    .body(htmlBody)
                    .fromAddress(senderAddress)
                    .simpleEmail(simpleEmail)
                    .build();

            DirectMessageConfiguration directMessageConfiguration = DirectMessageConfiguration.builder()
                    .emailMessage(emailMessage)
                    .build();

            MessageRequest messageRequest = MessageRequest.builder()
                    .addresses(addressMap)
                    .messageConfiguration(directMessageConfiguration)
                    .build();

            SendMessagesRequest messagesRequest = SendMessagesRequest.builder()
                    .applicationId(appId)
                    .messageRequest(messageRequest)
                    .build();

            pinpoint.sendMessages(messagesRequest);


        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[pinpoint.java2.send_email.main]
}
