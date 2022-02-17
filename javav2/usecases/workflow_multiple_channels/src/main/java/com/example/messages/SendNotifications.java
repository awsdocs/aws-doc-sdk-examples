/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.messages;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpointsmsvoice.PinpointSmsVoiceClient;
import software.amazon.awssdk.services.pinpointsmsvoice.model.SSMLMessageType;
import software.amazon.awssdk.services.pinpointsmsvoice.model.VoiceMessageContent;
import software.amazon.awssdk.services.pinpointsmsvoice.model.SendVoiceMessageRequest;
import software.amazon.awssdk.services.pinpointsmsvoice.model.PinpointSmsVoiceException;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SendRawEmailRequest;
import software.amazon.awssdk.services.ses.model.RawMessage;
import software.amazon.awssdk.services.ses.model.SesException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.io.IOException;
import java.io.StringReader;
import java.io.ByteArrayOutputStream;

public class SendNotifications {

    public String handleEmailMessage(String myDom) throws JDOMException, IOException, MessagingException {

        String myEmail = "";
        SesClient client = SesClient.builder()
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // Get the list of children elements.
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {

            myEmail = element.getChildText("Email");
            sendEmailMessage(client, myEmail);
        }
        client.close();
        return myEmail;
    }


    public String handleTextMessage(String myDom) throws JDOMException, IOException{

        String mobileNum = "";
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // get the list of children agent elements.
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {

            mobileNum = element.getChildText("Mobile");
            publishTextSMS(snsClient, mobileNum);
        }
        snsClient.close();
        return mobileNum;
    }

    public String handleVoiceMessage(String myDom) throws JDOMException, IOException{

        String mobileNum = "";
        List<String> listVal = new ArrayList<>();
        listVal.add("application/json");

        Map<String, List<String>> values = new HashMap<>();
        values.put("Content-Type", listVal);

        ClientOverrideConfiguration config2 = ClientOverrideConfiguration.builder()
                .headers(values)
                .build();

        PinpointSmsVoiceClient client = PinpointSmsVoiceClient.builder()
                .overrideConfiguration(config2)
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // get a list of children elements.
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {

            mobileNum = element.getChildText("Phone");
            sendVoiceMsg( client, mobileNum);
        }
        client.close();
        return mobileNum;
      }

    private void sendVoiceMsg(PinpointSmsVoiceClient client, String mobileNumber) {

        String languageCode = "en-US";
        String voiceName = "Matthew";

        String originationNumber = "+1-204-817-9095";
        String destinationNumber = mobileNumber;
        String ssmlMessage = "<speak>Please be advised that your student was marked absent from school today.</speak>";

        // Send a voice message from a Lambda function.
        try {
            SSMLMessageType ssmlMessageType = SSMLMessageType.builder()
                    .languageCode(languageCode)
                    .text(ssmlMessage)
                    .voiceId(voiceName)
                    .build();

            VoiceMessageContent content = VoiceMessageContent.builder()
                    .ssmlMessage(ssmlMessageType)
                    .build();

            SendVoiceMessageRequest voiceMessageRequest = SendVoiceMessageRequest.builder()
                    .destinationPhoneNumber(destinationNumber)
                    .originationPhoneNumber(originationNumber)
                    .content(content)
                    .build();

            client.sendVoiceMessage(voiceMessageRequest);

        } catch (PinpointSmsVoiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private void publishTextSMS(SnsClient snsClient, String phoneNumber) {

        String message = "Please be advised that your student was marked absent from school today.";

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(phoneNumber)
                    .build();

            snsClient.publish(request);

        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    private void sendEmailMessage(SesClient client, String recipient) throws MessagingException, IOException {

            // The email body for non-HTML email clients.
            String bodyText = "Hello,\r\n" + "Please be advised that your student was marked absent from school today. ";

            // The HTML body of the email.
            String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                    + "<p>Please be advised that your student was marked absent from school today.</p>" + "</body>" + "</html>";

            String sender = "scmacdon@amazon.com";
            String subject = "School Attendence";

            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session);

            // Add subject, from and to lines.
            message.setSubject(subject, "UTF-8");
            message.setFrom(new InternetAddress(sender));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

            // Create a multipart/alternative child container.
            MimeMultipart msgBody = new MimeMultipart("alternative");

            // Create a wrapper for the HTML and text parts.
            MimeBodyPart wrap = new MimeBodyPart();

            // Define the text part.
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(bodyText, "text/plain; charset=UTF-8");

            // Define the HTML part.
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(bodyHTML, "text/html; charset=UTF-8");

            // Add the text and HTML parts to the child container.
            msgBody.addBodyPart(textPart);
            msgBody.addBodyPart(htmlPart);

            // Add the child container to the wrapper object.
            wrap.setContent(msgBody);

            // Create a multipart/mixed parent container.
            MimeMultipart msg = new MimeMultipart("mixed");

            // Add the parent container to the message.
            message.setContent(msg);

            // Add the multipart/alternative part to the message.
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
        }
    }

