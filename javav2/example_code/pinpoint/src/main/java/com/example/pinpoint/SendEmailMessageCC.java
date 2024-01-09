//snippet-sourcedescription:[SendEmailMessageCC.java demonstrates how to send an email message which includes CC values.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Pinpoint]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.pinpoint;

//snippet-start:[pinpoint.java2.send_emailcc.main]
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class SendEmailMessageCC {

    // The character encoding the you want to use for the subject line and
    // message body of the email.
    public static String charset = "UTF-8";

    // The body of the email for recipients whose email clients support HTML content.
    static final String htmlBody = "<h1>Amazon Pinpoint test (AWS SDK for Java 2.x)</h1>"
        + "<p>This email was sent through the <a href='https://aws.amazon.com/pinpoint/'>"
        + "Amazon Pinpoint</a> Email API using the "
        + "<a href='https://aws.amazon.com/sdk-for-java/'>AWS SDK for Java 2.x</a>";

    public static void main(String[] args) {
        final String usage = """

            Usage:    <subject> <appId> <senderAddress> <toAddress> <ccAddress1> <ccAddress2>

            Where:
               subject - The email subject to use.
               appId - The Amazon Pinpoint project/application ID to use when you send this message
               senderAddress - The from address. This address has to be verified in Amazon Pinpoint in the region you're using to send email\s
               toAddress - The to address. This address has to be verified in Amazon Pinpoint in the region you're using to send email\s
               ccAddress1 - The first CC address.
               ccAddress2 - The second CC address.
            """;

        if (args.length != 6) {
             System.out.println(usage);
             System.exit(1);
        }

        String subject = args[0];
        String appId = args[1];
        String senderAddress = args[2];
        String toAddress = args[3];
        String ccAddress1 = args[4];
        String ccAddress2 = args[5];

        System.out.println("Sending a message");
        PinpointClient pinpoint = PinpointClient.builder()
            .region(Region.US_EAST_1)
            .build();

        ArrayList<String> ccList = new ArrayList<>();
        ccList.add(ccAddress1);
        ccList.add(ccAddress2);
        sendEmail(pinpoint, subject, appId, senderAddress, toAddress, ccList);
        pinpoint.close();
    }

    public static void sendEmail(PinpointClient pinpoint,
                                 String subject,
                                 String appId,
                                 String senderAddress,
                                 String toAddress,
                                 ArrayList<String> ccAddresses) {

        try {
            Map<String, AddressConfiguration> addressMap = new HashMap<>();
            AddressConfiguration addressConfig = AddressConfiguration.builder()
                .channelType(ChannelType.EMAIL)
                .build();

            addressMap.put(toAddress, addressConfig);

            // Create a separate AddressConfiguration for CC recipients.
            AddressConfiguration ccAddressConfig = AddressConfiguration.builder()
                .channelType(ChannelType.EMAIL)
                .build();

            // Add CC addresses to the addressMap.
            for (String ccAddress : ccAddresses) {
                addressMap.put(ccAddress, ccAddressConfig);
            }

            SimpleEmailPart emailPart = SimpleEmailPart.builder()
                .data(htmlBody)
                .charset(charset)
                .build();

            SimpleEmailPart subjectPart = SimpleEmailPart.builder()
                .data(subject)
                .charset(charset)
                .build();

            SimpleEmail simpleEmail = SimpleEmail.builder()
                .htmlPart(emailPart)
                .subject(subjectPart)
                .build();

            EmailMessage emailMessage = EmailMessage.builder()
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
            System.out.println("Message Sent");
        } catch (PinpointException e) {
            // Handle exception
            e.printStackTrace();
        }
    }
}
//snippet-end:[pinpoint.java2.send_emailcc.main]
