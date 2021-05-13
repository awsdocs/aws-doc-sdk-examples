// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SendMessage.java demonstrates how to send an email message by using the Amazon Simple Email Service (Amazon SES).]
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

// snippet-start:[ses.java2.sendmessage.complete]
package com.example.ses;

// snippet-start:[ses.java2.sendmessage.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SesException;
// snippet-end:[ses.java2.sendmessage.import]

/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class SendMessage {

    public static void main(String[] args) throws IOException {

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

    } catch (IOException | MessagingException e) {
        e.getStackTrace();
    }
  }

    // snippet-start:[ses.java2.sendmessage.main]
    public static void send(SesClient client,
                            String sender,
                            String recipient,
                            String subject,
                            String bodyText,
                            String bodyHTML
                            ) throws AddressException, MessagingException, IOException {

        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // Add subject, from and to lines
        message.setSubject(subject, "UTF-8");
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

        // Create a multipart/alternative child container
        MimeMultipart msgBody = new MimeMultipart("alternative");

        // Create a wrapper for the HTML and text parts
        MimeBodyPart wrap = new MimeBodyPart();

        // Define the text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(bodyText, "text/plain; charset=UTF-8");

        // Define the HTML part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

        // Add the text and HTML parts to the child container
        msgBody.addBodyPart(textPart);
        msgBody.addBodyPart(htmlPart);

        // Add the child container to the wrapper object
        wrap.setContent(msgBody);

        // Create a multipart/mixed parent container
        MimeMultipart msg = new MimeMultipart("mixed");

        // Add the parent container to the message
        message.setContent(msg);

        // Add the multipart/alternative part to the message
        msg.addBodyPart(wrap);

        try {
            System.out.println("Attempting to send an email through Amazon SES " + "using the AWS SDK for Java...");

             ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             message.writeTo(outputStream);
             ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());

             byte[] arr = new byte[buf.remaining()];
             buf.get(arr);

             SdkBytes data = SdkBytes.fromByteArray(arr);
             RawMessage rawMessage = RawMessage.builder()
                    .data(data)
                    .build();

             SendRawEmailRequest rawEmailRequest = SendRawEmailRequest.builder()
                    .rawMessage(rawMessage)
                    .build();

             client.sendRawEmail(rawEmailRequest);

         } catch (SesException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
         }

        // snippet-end:[ses.java2.sendmessage.main]
    }
}
// snippet-end:[ses.java2.sendmessage.complete]