/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.cloudformation.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.regions.Region;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudFormationTest {
    private static  CloudFormationClient cfClient;
    private static String stackName = "";
    private static String roleARN = "";
    private static String location = "";
    private static String key = "";
    private static String value = "";

    @BeforeAll
    public static void setUp() {
        cfClient = CloudFormationClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        stackName = values.getStackName();
        roleARN = values.getRoleARN();
        location= values.getLocation();
        key= values.getKey();
        value= values.getValue();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = CloudFormationTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            stackName = prop.getProperty("stackName");
            roleARN = prop.getProperty("roleARN");
            location = prop.getProperty("location");
            key = prop.getProperty("key");
            value = prop.getProperty("value");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateStack() {
        assertDoesNotThrow(() ->CreateStack.createCFStack(cfClient, stackName, roleARN, location, key, value));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeStacks() {
        assertDoesNotThrow(() ->DescribeStacks.describeAllStacks(cfClient));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void GetTemplate() {
        assertDoesNotThrow(() ->GetTemplate.getSpecificTemplate(cfClient, stackName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DeleteStack(){
        assertDoesNotThrow(() ->DeleteStack.deleteSpecificTemplate(cfClient, stackName));
        System.out.println("Test 4 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/cloudformation";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cloudformation (an AWS Secrets Manager secret)")
    class SecretValues {
        private String stackName;
        private String roleARN;
        private String location;

        private String key;

        private String value;

        public String getStackName() {
            return stackName;
        }

        public String getRoleARN() {
            return roleARN;
        }

        public String getLocation() {
            return location;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

}
