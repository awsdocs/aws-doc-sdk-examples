// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SendMessage.java demonstrates how to send an email message by using the Amazon Simple Email Service (Amazon SES) and a SendEmailRequest object.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/06/2020]
// snippet-sourceauthor:[AWS-scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.ses;

// snippet-start:[ses.java2.sendmessage.request.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.Body;
import javax.mail.MessagingException;
// snippet-end:[ses.java2.sendmessage.request.import]

public class SendMessageEmailRequest {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    SendMessage <sender> <recipient> <subject> \n\n" +
                "Where:\n" +
                "    sender - an email address that represents the sender. \n"+
                "    recipient -  an email address that represents the recipient. \n"+
                "    subject - the  subject line. \n" ;

          if (args.length != 3) {
            System.out.println(USAGE);
             System.exit(1);
           }

        String sender = args[0];
        String recipient = args[1];
        String subject = args[2];

        Region region = Region.US_EAST_1;
        SesClient client = SesClient.builder()
                .region(region)
                .build();

        // The email body for non-HTML email clients
        String bodyText = "Hello,\r\n" + "See the list of customers. ";

        // The HTML body of the email
        String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                + "<p> See the list of customers.</p>" + "</body>" + "</html>";

        try {
            send(client, sender, recipient, subject, bodyText, bodyHTML);
            client.close();
            System.out.println("Done");

        } catch (MessagingException e) {
            e.getStackTrace();
        }
    }

    // snippet-start:[ses.java2.sendmessage.request.main]
    public static void send(SesClient client,
                            String sender,
                            String recipient,
                            String subject,
                            String bodyText,
                            String bodyHTML
    ) throws MessagingException {

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

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(destination)
                .message(msg)
                .source(sender)
                .build();

        try {
            System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");
            client.sendEmail(emailRequest);

        } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        // snippet-end:[ses.java2.sendmessage.request.main]
    }
}

