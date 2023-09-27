/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.mediaconvert.CreateJob;
import com.example.mediaconvert.GetEndpointURL;
import com.example.mediaconvert.GetJob;
import com.example.mediaconvert.ListJobs;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonMediaConvertTest {
    private static MediaConvertClient mc ;
    private static Region region ;
    private static String mcRoleARN = "";
    private static String fileInput = "";
    private static String jobId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        region = Region.US_WEST_2;
        mc = MediaConvertClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        mcRoleARN = values.getMcRoleARN();
        fileInput = values.getFileInput();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonMediaConvertTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            mcRoleARN = prop.getProperty("mcRoleARN");
            fileInput = prop.getProperty("fileInput");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateJob() {
        jobId = CreateJob.createMediaJob(mc, mcRoleARN, fileInput);
        assertFalse(jobId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void GetEndpointURL() {
        assertDoesNotThrow(() ->GetEndpointURL.getEndpoint(mc));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListJobs() {
        assertDoesNotThrow(() ->ListJobs.listCompleteJobs(mc));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void GetJob() {
        assertDoesNotThrow(() -> GetJob.getSpecificJob(mc, jobId));
        System.out.println("Test 4 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/mediaconvert";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/mediaconvert (an AWS Secrets Manager secret)")
    class SecretValues {
        private String mcRoleARN;
        private String fileInput;

        public String getMcRoleARN() {
            return mcRoleARN;
        }

        public String getFileInput() {
            return fileInput;
        }

    }

}

