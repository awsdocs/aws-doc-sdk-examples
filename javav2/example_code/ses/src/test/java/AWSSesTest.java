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
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sesv2.SesV2Client;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AWSSesTest {

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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        sesv2Client =  SesV2Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        sender = values.getSender();
        recipient = values.getRecipient();
        subject = values.getSubject();
        fileLocation= values.getFileLocation();
        templateName = values.getTemplateName();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*


        try (InputStream input = AWSSesTest.class.getClassLoader().getResourceAsStream("config.properties")) {
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
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void SendMessage() {
        assertDoesNotThrow(() -> SendMessage.send(client, sender,recipient, subject, bodyText, bodyHTML));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void SendMessageV2() {
        assertDoesNotThrow(() -> SendEmail.send(sesv2Client, sender, recipient, subject, bodyHTML));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void SendMessageAttachment() {
        assertDoesNotThrow(() -> SendMessageAttachment.sendemailAttachment(client, sender, recipient, subject, bodyText, bodyHTML, fileLocation));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void SendMessageAttachmentV2() {
        assertDoesNotThrow(() -> com.example.sesv2.SendMessageAttachment.sendEmailAttachment(sesv2Client, sender, recipient, subject, bodyHTML, fileLocation));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void SendMessageTemplateV2() {
        assertDoesNotThrow(() -> SendEmailTemplate.send(sesv2Client, sender, recipient, templateName));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListIdentities() {
        assertDoesNotThrow(() -> ListIdentities.listSESIdentities(client));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void ListEmailIdentities() {
        assertDoesNotThrow(() -> ListEmailIdentities.listSESIdentities(sesv2Client));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void ListEmailTemplates() {
        assertDoesNotThrow(() -> ListTemplates.listAllTemplates(sesv2Client));
        System.out.println("Test 8 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/ses";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/ses (an AWS Secrets Manager secret)")
    class SecretValues {
        private String sender;
        private String recipient;
        private String subject;

        private String fileLocation;

        private String templateName;

        public String getSender() {
            return sender;
        }

        public String getRecipient() {
            return recipient;
        }

        public String getSubject() {
            return subject;
        }

        public String getFileLocation() {
            return fileLocation;
        }

        public String getTemplateName() {
            return templateName;
        }
    }
}
