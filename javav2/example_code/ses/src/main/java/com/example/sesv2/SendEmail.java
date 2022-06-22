// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SendMessage.java demonstrates how to send an email message by using the SesV2Client.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sesv2;

// snippet-start:[ses.java2.sendmessage.sesv2.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.model.Body;
import software.amazon.awssdk.services.sesv2.model.Content;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.Message;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sesv2.SesV2Client;
// snippet-end:[ses.java2.sendmessage.sesv2.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class SendEmail {

        public static void main(String[] args) {

                final String usage = "\n" +
                        "Usage:\n" +
                        "    <sender> <recipient> <subject> \n\n" +
                        "Where:\n" +
                        "    sender - An email address that represents the sender. \n"+
                        "    recipient -  An email address that represents the recipient. \n"+
                        "    subject - The  subject line. \n" ;

               if (args.length != 3) {
                      System.out.println(usage);
                      System.exit(1);
               }

                String sender = args[0];
                String recipient = args[1];
                String subject = args[2];

                Region region = Region.US_EAST_1;
                SesV2Client sesv2Client = SesV2Client.builder()
                        .region(region)
                        .credentialsProvider(ProfileCredentialsProvider.create())
                        .build();

                // The HTML body of the email.
                String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                        + "<p> See the list of customers.</p>" + "</body>" + "</html>";

                send(sesv2Client, sender, recipient, subject, bodyHTML);
        }

        // snippet-start:[ses.java2.sendmessage.sesv2.main]
        public static void send(SesV2Client client,
                                String sender,
                                String recipient,
                                String subject,
                                String bodyHTML
        ){

                Destination destination = Destination.builder()
                        .toAddresses(recipient)
                        .build();

                Content content = Content.builder()
                        .data(bodyHTML)
                        .build();

                Content sub = Content.builder()
                        .data(subject)
                        .build();

                Body body = Body.builder()
                        .html(content)
                        .build();

                Message msg = Message.builder()
                        .subject(sub)
                        .body(body)
                        .build();

                EmailContent emailContent = EmailContent.builder()
                        .simple(msg)
                        .build();

                SendEmailRequest emailRequest = SendEmailRequest.builder()
                        .destination(destination)
                        .content(emailContent)
                        .fromEmailAddress(sender)
                        .build();

                try {
                        System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
                        client.sendEmail(emailRequest);
                        System.out.println("email was sent");

                } catch (SesV2Exception e) {
                        System.err.println(e.awsErrorDetails().errorMessage());
                        System.exit(1);
                }
        }
        // snippet-end:[ses.java2.sendmessage.sesv2.main]
}
