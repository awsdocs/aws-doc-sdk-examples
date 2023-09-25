/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.textract.AnalyzeDocument;
import com.example.textract.DetectDocumentText;
import com.example.textract.DetectDocumentTextS3;
import com.example.textract.StartDocumentAnalysis;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.textract.TextractClient;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;


/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TextractTest {
    private static TextractClient textractClient;
    private static Region region;
    private static String sourceDoc = "";
    private static String bucketName = "";
    private static String docName = "";

    @BeforeAll
    public static void setUp() throws IOException {
        region = Region.US_WEST_2;
        textractClient = TextractClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        sourceDoc = values.getSourceDoc();
        bucketName =  values.getBucketName();
        docName = values.getDocName();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = TextractTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            sourceDoc = prop.getProperty("sourceDoc");
            bucketName = prop.getProperty("bucketName");
            docName = prop.getProperty("docName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void AnalyzeDocument() {
        assertDoesNotThrow(() ->AnalyzeDocument.analyzeDoc(textractClient, sourceDoc));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DetectDocumentText() {
        assertDoesNotThrow(() ->DetectDocumentText.detectDocText(textractClient, sourceDoc));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DetectDocumentTextS3() {
        assertDoesNotThrow(() ->DetectDocumentTextS3.detectDocTextS3(textractClient, bucketName, docName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void StartDocumentAnalysis() {
        assertDoesNotThrow(() ->StartDocumentAnalysis.startDocAnalysisS3(textractClient, bucketName, docName));
        System.out.println("Test 4 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/textract";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/textract (an AWS Secrets Manager secret)")
    class SecretValues {
        private String sourceDoc;
        private String bucketName;
        private String docName;

        public String getSourceDoc() {
            return sourceDoc;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getDocName() {
            return docName;
        }

    }
}

