// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.lex.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
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
public class AmazonLexTest {
    private static final Logger logger = LoggerFactory.getLogger(AmazonLexTest.class);
    private static LexModelBuildingClient lexClient;
    private static String botName = "";
    private static String intentName = "";
    private static String intentVersion = "";

    @BeforeAll
    public static void setUp() {
        lexClient = LexModelBuildingClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        botName = values.getBotName();
        intentName = values.getIntentName();
        intentVersion = values.getIntentVersion();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testPutBot() {
        assertDoesNotThrow(() -> PutBot.createBot(lexClient, botName, intentName, intentVersion));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testGetBots() {
        assertDoesNotThrow(() -> GetBots.getAllBots(lexClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testGetIntent() {
        assertDoesNotThrow(() -> GetIntent.getSpecificIntent(lexClient, intentName, intentVersion));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetSlotTypes() {
        assertDoesNotThrow(() -> GetSlotTypes.getSlotsInfo(lexClient));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testGetBotStatus() {
        assertDoesNotThrow(() -> GetBotStatus.getStatus(lexClient, botName));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDeleteBot() {
        assertDoesNotThrow(() -> DeleteBot.deleteSpecificBot(lexClient, botName));
        logger.info("Test 6 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/lex";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/lex (an AWS Secrets Manager secret)")
    class SecretValues {
        private String intentName;
        private String botName;
        private String intentVersion;

        public String getIntentName() {
            return intentName;
        }

        public String getBotName() {
            return botName;
        }

        public String getIntentVersion() {
            return intentVersion;
        }

    }
}
