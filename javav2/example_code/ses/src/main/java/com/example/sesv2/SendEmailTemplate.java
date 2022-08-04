// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SendEmailTemplate.java demonstrates how to send an email message based on a template by using the SesV2Client.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-keyword:[Amazon Simple Email Service]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.sesv2;

// snippet-start:[ses.java2.sendmessage.template.sesv2.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sesv2.model.Destination;
import software.amazon.awssdk.services.sesv2.model.EmailContent;
import software.amazon.awssdk.services.sesv2.model.SendEmailRequest;
import software.amazon.awssdk.services.sesv2.model.SesV2Exception;
import software.amazon.awssdk.services.sesv2.SesV2Client;
import software.amazon.awssdk.services.sesv2.model.Template;
// snippet-end:[ses.java2.sendmessage.template.sesv2.import]

/**
 * Before running this AWS SDK for Java (v2) example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * Also, make sure that you create a template. See the following documentation topic:
 *
 * https://docs.aws.amazon.com/ses/latest/dg/send-personalized-email-api.html
 */

public class SendEmailTemplate {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <template> <sender> <recipient> \n\n" +
            "Where:\n" +
            "    template - The name of the email template" +
            "    sender - An email address that represents the sender. \n"+
            "    recipient - An email address that represents the recipient. \n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String templateName = args[0];
        String sender = args[1];
        String recipient = args[2];
        Region region = Region.US_EAST_1;
        SesV2Client sesv2Client = SesV2Client.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        send(sesv2Client, sender, recipient, templateName);
    }

    // snippet-start:[ses.java2.sendmessage.template.sesv2.main]
    public static void send(SesV2Client client, String sender, String recipient, String templateName){

        Destination destination = Destination.builder()
            .toAddresses(recipient)
            .build();

        /*
         Specify both name and favoriteanimal in your code when defining the Template object.
         If you DO NOT specify all variables in the template - SES does not send the email.
        */
        Template myTemplate = Template.builder()
            .templateName(templateName)
            .templateData("{\n" +
              "  \"name\": \"Jason\"\n," +
              "  \"favoriteanimal\": \"Cat\"\n" +
              "}")
            .build();

        EmailContent emailContent = EmailContent.builder()
            .template(myTemplate)
            .build();

        SendEmailRequest emailRequest = SendEmailRequest.builder()
            .destination(destination)
            .content(emailContent)
            .fromEmailAddress(sender)
            .build();

        try {
            System.out.println("Attempting to send an email based on a template using the AWS SDK for Java V2...");
            client.sendEmail(emailRequest);
            System.out.println("email based on a template was sent");

        } catch (SesV2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[ses.java2.sendmessage.template.sesv2.main]
}
