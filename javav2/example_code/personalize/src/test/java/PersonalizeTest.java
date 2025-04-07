// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.personalize.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonalizeTest {
    private static final Logger logger = LoggerFactory.getLogger(PersonalizeTest.class);
    private static PersonalizeRuntimeClient personalizeRuntimeClient;
    private static PersonalizeClient personalizeClient;
    private static String datasetGroupArn = "";
    private static String solutionArn = "";
    private static String existingSolutionArn = "";
    private static String solutionVersionArn = "";
    private static String recipeArn = "";
    private static String solutionName = "";
    private static String campaignName = "";
    private static String campaignArn = "";
    private static String userId = "";
    private static String existingCampaignName = "";

    @BeforeAll
    public static void setUp() {
        personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        personalizeClient = PersonalizeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        datasetGroupArn = values.getDatasetGroupArn();
        solutionVersionArn = values.getSolutionVersionArn();
        recipeArn = values.getRecipeArn();
        solutionName = values.getSolutionName() + java.util.UUID.randomUUID();
        ;
        userId = values.getUserId();
        campaignName = values.getCampaignName() + java.util.UUID.randomUUID();
        ;
        existingSolutionArn = values.getExistingSolutionArn();
        existingCampaignName = values.getExistingCampaignName();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateSolution() {
        solutionArn = CreateSolution.createPersonalizeSolution(personalizeClient, datasetGroupArn, solutionName,
                recipeArn);
        assertFalse(solutionArn.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testListSolutions() {
        assertDoesNotThrow(() -> ListSolutions.listAllSolutions(personalizeClient, datasetGroupArn));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeSolution() {
        assertDoesNotThrow(() -> DescribeSolution.describeSpecificSolution(personalizeClient, solutionArn));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testCreateCampaign() {
        campaignArn = CreateCampaign.createPersonalCompaign(personalizeClient, solutionVersionArn, campaignName);
        assertFalse(campaignArn.isEmpty());
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListCampaigns() {
        assertDoesNotThrow(() -> ListCampaigns.listAllCampaigns(personalizeClient, existingSolutionArn));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDescribeRecipe() {
        assertDoesNotThrow(() -> DescribeRecipe.describeSpecificRecipe(personalizeClient, recipeArn));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testListRecipes() {
        assertDoesNotThrow(() -> ListRecipes.listAllRecipes(personalizeClient));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testListDatasetGroups() {
        assertDoesNotThrow(() -> ListDatasetGroups.listDSGroups(personalizeClient));
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDeleteSolution() {
        assertDoesNotThrow(() -> DeleteSolution.deleteGivenSolution(personalizeClient, solutionArn));
        logger.info("Test 9 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDeleteCampaign() {
        assertDoesNotThrow(() -> {
            DeleteCampaign.waitForCampaignToBeDeletable(personalizeClient, campaignArn);
            DeleteCampaign.deleteSpecificCampaign(personalizeClient, campaignArn);
        });
        logger.info("Test 10 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/personalize";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/personalize (an AWS Secrets Manager secret)")
    class SecretValues {
        private String solutionName;
        private String campaignName;
        private String existingSolutionArn;

        private String solutionVersionArn;

        private String existingCampaignName;

        private String datasetGroupArn;

        private String recipeArn;

        private String userId;

        public String getSolutionName() {
            return solutionName;
        }

        public String getCampaignName() {
            return campaignName;
        }

        public String getExistingSolutionArn() {
            return existingSolutionArn;
        }

        public String getSolutionVersionArn() {
            return solutionVersionArn;
        }

        public String getDatasetGroupArn() {
            return datasetGroupArn;
        }

        public String getExistingCampaignName() {
            return existingCampaignName;
        }

        public String getRecipeArn() {
            return recipeArn;
        }

        public String getUserId() {
            return userId;
        }
    }
}
