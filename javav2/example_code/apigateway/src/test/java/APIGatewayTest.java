// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.gateway.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(APIGatewayTest.class);
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
        restApiName = values.getRestApiName() + randomNum;
        stageName = values.getStageName();
    }

    @Test
    @Order(1)
    public void testCreateRestApi() {
        newApiId = CreateRestApi.createAPI(apiGateway, restApiId, restApiName);
        assertFalse(newApiId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Order(2)
    public void testCreateDeployment() {
        deploymentId = CreateDeployment.createNewDeployment(apiGateway, newApiId, stageName);
        assertFalse(deploymentId.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Order(3)
    public void testGetDeployments() {
        assertDoesNotThrow(() -> GetDeployments.getAllDeployments(apiGateway, newApiId));
        logger.info("Test 3 passed");
    }

    @Test
    @Order(4)
    public void testGetStages() {
        assertDoesNotThrow(() -> GetStages.getAllStages(apiGateway, newApiId));
        logger.info("Test 4 passed");
    }

    @Test
    @Order(5)
    public void testDeleteRestApi() {
        assertDoesNotThrow(() -> DeleteRestApi.deleteAPI(apiGateway, newApiId));
        logger.info("Test 5 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
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
