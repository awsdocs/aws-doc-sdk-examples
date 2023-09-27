/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.secrets.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecretManagerTest {

    private static SecretsManagerClient secretsClient;
    private static String newSecretName="";
    private static String secretValue="";
    private static String secretARN="";
    private static String modSecretValue="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {
        Region region = Region.US_EAST_1;
        secretsClient = SecretsManagerClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();
        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        newSecretName = values.getNewSecretName()+randomNum;
        secretValue = values.getSecretValue();
        modSecretValue = values.getModSecretValue();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = SecretManagerTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            newSecretName = prop.getProperty("newSecretName");
            secretValue = prop.getProperty("secretValue");
            modSecretValue = prop.getProperty("modSecretValue");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }
    @Test
    @Order(1)
    public void CreateSecret() {
        secretARN = CreateSecret.createNewSecret(secretsClient, newSecretName,secretValue);
        assertFalse(secretARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(2)
    public void DescribeSecret() {
        assertDoesNotThrow(() ->DescribeSecret.describeGivenSecret(secretsClient, secretARN));
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(3)
    public void GetSecretValue() {
        assertDoesNotThrow(() ->GetSecretValue.getValue(secretsClient, secretARN));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(4)
    public void UpdateSecret() {
        assertDoesNotThrow(() ->UpdateSecret.updateMySecret(secretsClient,secretARN, modSecretValue));
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(5)
    public void ListSecrets() {
        assertDoesNotThrow(() ->ListSecrets.listAllSecrets(secretsClient));
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(6)
    public void DeleteSecret() {
        assertDoesNotThrow(() -> DeleteSecret.deleteSpecificSecret(secretsClient, secretARN));
        System.out.println("Test 7 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/secretmanager";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/secretmanager (an AWS Secrets Manager secret)")
    class SecretValues {
        private String newSecretName;
        private String secretValue;
        private String modSecretValue;

        public String getNewSecretName() {
            return newSecretName;
        }

        public String getSecretValue() {
            return secretValue;
        }

        public String getModSecretValue() {
            return modSecretValue;
        }
    }
}


