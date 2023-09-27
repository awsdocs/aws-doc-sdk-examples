/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.comprehend.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonComprehendTest {

    private static  ComprehendClient comClient;
    private static String text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing" ;
    private static String frText = "Il pleut aujourd'hui à Seattle" ;
    private static String dataAccessRoleArn;
    private static String s3Uri;
    private static String documentClassifierName;

    @BeforeAll
    public static void setUp() throws IOException {
        comClient = ComprehendClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        dataAccessRoleArn = values.getDataAccessRoleArn();
        s3Uri = values.getS3Uri();
        documentClassifierName = values.getDocumentClassifier();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonComprehendTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            dataAccessRoleArn = prop.getProperty("dataAccessRoleArn");
            s3Uri = prop.getProperty("s3Uri");
            documentClassifierName = prop.getProperty("documentClassifier");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("weathertop")
    @Tag("IntegrationTest")
    @Order(1)
    public void DetectEntities() {
        assertDoesNotThrow(() ->DetectEntities.detectAllEntities(comClient, text));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("weathertop")
    @Tag("IntegrationTest")
    @Order(2)
    public void DetectKeyPhrases() {
        assertDoesNotThrow(() ->DetectKeyPhrases.detectAllKeyPhrases(comClient, text));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("weathertop")
    @Tag("IntegrationTest")
    @Order(3)
    public void DetectLanguage() {
        assertDoesNotThrow(() ->DetectLanguage.detectTheDominantLanguage(comClient, frText));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("weathertop")
    @Tag("IntegrationTest")
    @Order(4)
    public void DetectSentiment() {
        assertDoesNotThrow(() ->DetectSentiment.detectSentiments(comClient, text));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("weathertop")
    @Tag("IntegrationTest")
    @Order(5)
    public void DetectSyntax() {
        assertDoesNotThrow(() -> DetectSyntax.detectAllSyntax(comClient, text));
        System.out.println("Test 5 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/comprehend";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/comprehend (an AWS Secrets Manager secret)")
    class SecretValues {
        private String dataAccessRoleArn;
        private String s3Uri;
        private String documentClassifier;

        public String getDataAccessRoleArn() {
            return dataAccessRoleArn;
        }

        public String getS3Uri() {
            return s3Uri;
        }

        public String getDocumentClassifier() {
            return documentClassifier;
        }
    }
}
