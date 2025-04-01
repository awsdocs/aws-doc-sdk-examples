// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.ses.ListIdentities;
import com.example.ses.SendMessage;
import com.example.ses.SendMessageAttachment;
import com.example.sesv2.ListEmailIdentities;
import com.example.sesv2.ListTemplates;
import com.example.sesv2.SendEmail;
import com.example.sesv2.SendEmailTemplate;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(AWSSesTest.class);
    private static SesClient client;
    private static SesV2Client sesv2Client;
    private static String sender = "";
    private static String recipient = "";
    private static String subject = "";
    private static String fileLocation = "";
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
                .build();

        sesv2Client = SesV2Client.builder()
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        sender = values.getSender();
        recipient = values.getRecipient();
        subject = values.getSubject();
        fileLocation = values.getFileLocation();
        templateName = values.getTemplateName();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testSendMessage() {
        assertDoesNotThrow(() -> SendMessage.send(client, sender, recipient, subject, bodyText, bodyHTML));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testSendMessageV2() {
        assertDoesNotThrow(() -> SendEmail.send(sesv2Client, sender, recipient, subject, bodyHTML));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testSendMessageTemplateV2() {
        assertDoesNotThrow(() -> SendEmailTemplate.send(sesv2Client, sender, recipient, templateName));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListIdentities() {
        assertDoesNotThrow(() -> ListIdentities.listSESIdentities(client));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListEmailIdentities() {
        assertDoesNotThrow(() -> ListEmailIdentities.listSESIdentities(sesv2Client));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListEmailTemplates() {
        assertDoesNotThrow(() -> ListTemplates.listAllTemplates(sesv2Client));
        logger.info("Test 6 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
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
