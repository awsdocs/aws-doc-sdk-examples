/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.gateway.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.apigateway.ApiGatewayClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class APIGatewayTest {

    private static ApiGatewayClient apiGateway;
    private static String restApiId = "";
    private static String resourceId = "";
    private static String httpMethod = "";
    private static String restApiName = "";
    private static String stageName = "";
    private static String newApiId = "";
    private static String deploymentId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_EAST_1;
        apiGateway = ApiGatewayClient.builder().region(region).build();
        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        restApiId = values.getRestApiId();
        httpMethod = values.getHttpMethod();
        restApiName = values.getRestApiName()+randomNum;
        stageName = values.getStageName();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = APIGatewayTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            restApiId = prop.getProperty("restApiId");
            resourceId = prop.getProperty("resourceId");
            httpMethod = prop.getProperty("httpMethod");
            restApiName = prop.getProperty("restApiName");
            stageName = prop.getProperty("stageName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Order(1)
    public void CreateRestApi() {
        newApiId = CreateRestApi.createAPI(apiGateway, restApiId, restApiName);
        assertFalse(newApiId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(2)
    public void CreateDeployment() {
        deploymentId = CreateDeployment.createNewDeployment(apiGateway, newApiId, stageName);
        assertFalse(deploymentId.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(3)
    public void GetDeployments() {
        assertDoesNotThrow(() ->GetDeployments.getAllDeployments(apiGateway, newApiId));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(4)
    public void GetStages() {
        assertDoesNotThrow(() ->GetStages.getAllStages(apiGateway, newApiId));
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(5)
    public void DeleteRestApi() {
        assertDoesNotThrow(() ->DeleteRestApi.deleteAPI(apiGateway, newApiId));
        System.out.println("Test 9 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/apigateway";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/apigateway (an AWS Secrets Manager secret)")
    class SecretValues {
        private String restApiId;
        private String restApiName;
        private String httpMethod;

        private String stageName;

        public String getRestApiId() {
            return restApiId;
        }

        public String getRestApiName() {
            return restApiName;
        }

        public String getHttpMethod() {
            return httpMethod;
        }

        public String getStageName() {
            return stageName;
        }
    }
}

