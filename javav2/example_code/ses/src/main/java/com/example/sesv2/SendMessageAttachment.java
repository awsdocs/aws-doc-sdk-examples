// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SendMessageAttachment.java demonstrates how to send an email message that contains an attachment by using the SesV2Client.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Email Service]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.example.sesv2;

// snippet-start:[ses.java2.sendmessage.request.sesv2.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.model.RawMessage;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.Properties;
// snippet-end:[ses.java2.sendmessage.request.sesv2.import]

public class SendMessageAttachment {

        public static void main(String[] args) throws MessagingException, IOException {

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
                String fileLocation = args[3];

                Region region = Region.US_EAST_1;
                SesV2Client sesv2Client = SesV2Client.builder()
                        .region(region)
                        .credentialsProvider(ProfileCredentialsProvider.create())
                        .build();

                // The HTML body of the email.
                String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                        + "<p> See the attachment.</p>" + "</body>" + "</html>";

                sendEmailAttachment(sesv2Client, sender, recipient, subject, bodyHTML, fileLocation );
        }

        // snippet-start:[ses.java2.sendmessage.request.sesv2.main]
        public static void sendEmailAttachment(SesV2Client sesv2Client,
                                               String sender,
                                               String recipient,
                                               String subject,
                                               String bodyHTML,
                                               String fileLocation) throws MessagingException, IOException {

                java.io.File theFile = new java.io.File(fileLocation);
                byte[] fileContent = Files.readAllBytes(theFile.toPath());
                Session session = Session.getDefaultInstance(new Properties());

                // Create a new MimeMessage object.
                MimeMessage message = new MimeMessage(session);

                // Add subject, from and to lines.
                message.setSubject(subject, "UTF-8");
                message.setFrom(new InternetAddress(sender));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

                // Create a multipart/alternative child container.
                MimeMultipart msgBody = new MimeMultipart("alternative");

                // Create a wrapper for the HTML and text parts.
                MimeBodyPart wrap = new MimeBodyPart();

                // Define the HTML part.
                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

                // Add the HTML parts to the child container.
               // msgBody.addBodyPart(textPart);
                msgBody.addBodyPart(htmlPart);

                // Add the child container to the wrapper object.
                wrap.setContent(msgBody);

                // Create a multipart/mixed parent container.
                MimeMultipart msg = new MimeMultipart("mixed");

                // Add the parent container to the message.
                message.setContent(msg);

                // Add the multipart/alternative part to the message.
                msg.addBodyPart(wrap);

                // Define the attachment.
                MimeBodyPart att = new MimeBodyPart();
                DataSource fds = new ByteArrayDataSource(fileContent, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                att.setDataHandler(new DataHandler(fds));

                String reportName = "WorkReport.xls";
                att.setFileName(reportName);

                // Add the attachment to the message.
                msg.addBodyPart(att);

                try {
                        System.out.println("Attempting to send an email through Amazon SES using the AWS SDK for Java...");
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        message.writeTo(outputStream);

                        ByteBuffer buf = ByteBuffer.wrap(outputStream.toByteArray());
                        byte[] arr = new byte[buf.remaining()];
                        buf.get(arr);

                        SdkBytes data = SdkBytes.fromByteArray(arr);
                        RawMessage rawMessage = RawMessage.builder()
                                .data(data)
                                .build();

                        EmailContent emailContent = EmailContent.builder()
                                .raw(rawMessage)
                                .build();

                        SendEmailRequest request = SendEmailRequest.builder()
                                        .content(emailContent)
                                        .build();

                        sesv2Client.sendEmail(request);

                } catch (SesV2Exception e ) {
                        System.err.println(e.awsErrorDetails().errorMessage());
                        System.exit(1);
                }
                System.out.println("The email message was successfully sent with an attachment");
        }
        // snippet-end:[ses.java2.sendmessage.request.sesv2.main]
}
