/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.ses.ListIdentities;
import com.example.ses.SendMessage;
import com.example.ses.SendMessageAttachment;
import com.example.sesv2.ListEmailIdentities;
import com.example.sesv2.ListTemplates;
import com.example.sesv2.SendEmail;
import com.example.sesv2.SendEmailTemplate;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SESTest {

    private static SesClient client ;
    private static SesV2Client sesv2Client ;
    private static String sender="";
    private static String recipient="";
    private static String subject="";
    private static String fileLocation="";
    private static String templateName = "";

    private static String bodyText = "Hello,\r\n" + "Please see the attached file for a list "
            + "of customers to contact.";

    // The HTML body of the email.
    private static String bodyHTML = "<html>" + "<head></head>" + "<body>" + "<h1>Hello!</h1>"
            + "<p>Please see the attached file for a " + "list of customers to contact.</p>" + "</body>" + "</html>";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        client = SesClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        sesv2Client =  SesV2Client.builder()
                 .region(Region.US_EAST_1)
                 .credentialsProvider(ProfileCredentialsProvider.create())
                 .build();

        try (InputStream input = SESTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            sender = prop.getProperty("sender");
            recipient = prop.getProperty("recipient");
            subject = prop.getProperty("subject");
            fileLocation= prop.getProperty("fileLocation");
            templateName = prop.getProperty("templateName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(client);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void SendMessage() {
        assertDoesNotThrow(() -> SendMessage.send(client, sender,recipient, subject,bodyText,bodyHTML));
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void SendMessageV2() {
        assertDoesNotThrow(() -> SendEmail.send(sesv2Client, sender, recipient, subject, bodyHTML));
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void SendMessageAttachment() {
        assertDoesNotThrow(() -> SendMessageAttachment.sendemailAttachment(client, sender, recipient, subject, bodyText, bodyHTML, fileLocation ));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void SendMessageAttachmentV2() {
        assertDoesNotThrow(() -> com.example.sesv2.SendMessageAttachment.sendEmailAttachment(sesv2Client, sender, recipient, subject, bodyHTML, fileLocation));
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void SendMessageTemplateV2() {
        assertDoesNotThrow(() -> SendEmailTemplate.send(sesv2Client, sender, recipient, templateName));
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListIdentities() {
        assertDoesNotThrow(() -> ListIdentities.listSESIdentities(client));
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(8)
    public void ListEmailIdentities() {
        assertDoesNotThrow(() -> ListEmailIdentities.listSESIdentities(sesv2Client));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(9)
    public void ListEmailTemplates() {
        assertDoesNotThrow(() -> ListTemplates.listALlTemplates(sesv2Client));
        System.out.println("Test 4 passed");
    }
}
