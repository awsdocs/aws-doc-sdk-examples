/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.personalize.*;
import com.google.gson.Gson;
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

    private static PersonalizeRuntimeClient personalizeRuntimeClient;
    private static  PersonalizeClient personalizeClient;
    private static String datasetGroupArn = "";
    private static String solutionArn = "";
    private static String existingSolutionArn = "";
    private static String solutionVersionArn = "";
    private static String recipeArn = "";
    private static String solutionName = "";
    private static String campaignName = "";
    private static String campaignArn = "";
    private static String userId = "";
    private static String existingCampaignName="";

    @BeforeAll
    public static void setUp() {
        personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.US_EAST_1)
            .build();

        personalizeClient = PersonalizeClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.US_EAST_1)
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        datasetGroupArn = values.getDatasetGroupArn();
        solutionVersionArn = values.getSolutionVersionArn();
        recipeArn = values.getRecipeArn();
        solutionName = values.getSolutionName()+ java.util.UUID.randomUUID();;
        userId = values.getUserId();
        campaignName= values.getCampaignName()+ java.util.UUID.randomUUID();;
        existingSolutionArn= values.getExistingSolutionArn();
        existingCampaignName = values.getExistingCampaignName();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
        /*

        try (InputStream input = PersonalizeTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            datasetGroupArn = prop.getProperty("datasetGroupArn");
            solutionVersionArn = prop.getProperty("solutionVersionArn");
            recipeArn = prop.getProperty("recipeArn");
            solutionName = prop.getProperty("solutionName")+ java.util.UUID.randomUUID();;
            userId = prop.getProperty("userId");
            campaignName= prop.getProperty("campaignName")+ java.util.UUID.randomUUID();;
            existingSolutionArn= prop.getProperty("existingSolutionArn");
            existingCampaignName = prop.getProperty("existingCampaignName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateSolution() {
        solutionArn = CreateSolution.createPersonalizeSolution(personalizeClient, datasetGroupArn, solutionName, recipeArn);
        assertFalse(solutionArn.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void ListSolutions() {
        assertDoesNotThrow(() ->ListSolutions.listAllSolutions(personalizeClient, datasetGroupArn));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeSolution() {
        assertDoesNotThrow(() ->DescribeSolution.describeSpecificSolution(personalizeClient, solutionArn));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void CreateCampaign() {
        campaignArn = CreateCampaign.createPersonalCompaign(personalizeClient, solutionVersionArn, campaignName);
        assertFalse(campaignArn.isEmpty());
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DescribeCampaign() throws InterruptedException {
        System.out.println("Wait 20 mins for resource to become available.");
        TimeUnit.MINUTES.sleep(20);
        assertDoesNotThrow(() ->DescribeCampaign.describeSpecificCampaign(personalizeClient, campaignArn));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void ListCampaigns() {
        assertDoesNotThrow(() ->ListCampaigns.listAllCampaigns(personalizeClient, existingSolutionArn));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DescribeRecipe() {
        assertDoesNotThrow(() ->DescribeRecipe.describeSpecificRecipe(personalizeClient, recipeArn));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void ListRecipes() {
        assertDoesNotThrow(() ->ListRecipes.listAllRecipes(personalizeClient));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void ListDatasetGroups() {
        assertDoesNotThrow(() ->ListDatasetGroups.listDSGroups(personalizeClient));
        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void DeleteSolution() {
        assertDoesNotThrow(() ->DeleteSolution.deleteGivenSolution(personalizeClient,solutionArn));
        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void GetRecommendations() {
        assertDoesNotThrow(() ->GetRecommendations.getRecs(personalizeRuntimeClient, campaignArn, userId));
        System.out.println("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void DeleteCampaign() {
        assertDoesNotThrow(() ->DeleteCampaign.deleteSpecificCampaign(personalizeClient, campaignArn));
        System.out.println("Test 12 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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
