/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.InputSource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.SesException;
import javax.mail.MessagingException;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;

public class SendNotifications {
    public int handleEmailMessage(String myDom) throws JDOMException, IOException, MessagingException {
        String myEmail;
        String name ;
        SesClient client = SesClient.builder()
                .region(Region.US_WEST_2)
                .build();

        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // Get the list of children elements.
        int countStudents = 0;
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {
            myEmail = element.getChildText("Email");
            name = element.getChildText("Name");
            sendEmail(client, myEmail, name);
            countStudents++;
        }
        client.close();
        return countStudents;
    }

    public void handleTextMessage(String myDom) throws JDOMException, IOException{
        String mobileNum;
        String name ;
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();

        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document jdomDocument = builder.build(new InputSource(new StringReader(myDom)));
        org.jdom2.Element root = jdomDocument.getRootElement();

        // get the list of children agent elements.
        List<org.jdom2.Element> students = root.getChildren("Student");
        for (org.jdom2.Element element : students) {
            mobileNum = element.getChildText("Mobile");
            name = element.getChildText("Name");
            publishTextSMS(snsClient, mobileNum, name);
        }
        snsClient.close();
    }

    private void publishTextSMS(SnsClient snsClient, String phoneNumber, String name) {
        String message = "Please be advised that "+name + " was marked absent from school today.";
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

    public void sendEmail(SesClient client, String recipient, String name) {
             // The HTML body of the email.
            String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
                    + "<p>Please be advised that "+name +" was marked absent from school today.</p>" + "</body>" + "</html>";

            String sender = "scmacdon@amazon.com";
            String subject = "School Attendance";

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

            software.amazon.awssdk.services.ses.model.Message msg = software.amazon.awssdk.services.ses.model.Message.builder()
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
        }
    }

