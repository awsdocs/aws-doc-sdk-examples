/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.lex.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
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

    private static LexModelBuildingClient lexClient;
    private static String botName = "";
    private static String intentName = "";
    private static String intentVersion = "";

    @BeforeAll
    public static void setUp() {
        lexClient = LexModelBuildingClient.builder()
           .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        botName = values.getBotName();
        intentName = values.getIntentName();
        intentVersion = values.getIntentVersion();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = AmazonLexTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            botName = prop.getProperty("botName");
            intentName = prop.getProperty("intentName");
            intentVersion = prop.getProperty("intentVersion");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

   @Test
   @Tag("IntegrationTest")
   @Order(1)
   public void PutBot() {
       assertDoesNotThrow(() ->PutBot.createBot(lexClient, botName, intentName, intentVersion));
       System.out.println("Test 1 passed");
   }

  @Test
  @Tag("IntegrationTest")
  @Order(2)
   public void GetBots() {
       assertDoesNotThrow(() -> GetBots.getAllBots(lexClient));
       System.out.println("Test 2 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(3)
   public void GetIntent() {
       assertDoesNotThrow(() ->GetIntent.getSpecificIntent(lexClient, intentName, intentVersion));
       System.out.println("Test 3 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(4)
   public void GetSlotTypes() {
       assertDoesNotThrow(() ->GetSlotTypes.getSlotsInfo(lexClient));
       System.out.println("Test 4 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(5)
   public void GetBotStatus() {
       assertDoesNotThrow(() ->GetBotStatus.getStatus(lexClient,botName));
       System.out.println("Test 5 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(6)
   public void DeleteBot() {
       assertDoesNotThrow(() ->DeleteBot.deleteSpecificBot(lexClient, botName));
       System.out.println("Test 6 passed");
   }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

