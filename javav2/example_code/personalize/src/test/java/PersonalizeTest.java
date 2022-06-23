import com.example.personalize.*;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.model.IngestionMode;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;
import software.amazon.awssdk.services.personalizeruntime.model.PredictedItem;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonalizeTest {

    private static PersonalizeRuntimeClient personalizeRuntimeClient;
    private static PersonalizeClient personalizeClient;
    private static PersonalizeEventsClient personalizeEventsClient;

    private static String newSolutionDatasetGroupArn = "";
    private static String newDatasetGroupName = "";

    private static String schemaName = "";
    private static String schemaLocation = "";

    private static String datasetImportJobName = "";
    private static String importDatasetArn = "";
    private static String s3BucketPath = "";
    private static String roleArn = "";

    private static String schemaArn = "";
    private static String newDatasetName = "";
    private static String newDatasetType = "";
    private static String existingDatasetGroup = "";

    private static String solutionArn = "";
    private static String existingSolutionVersionArn = "";
    private static String recipeArn = "";
    private static String newSolutionName = "";
    private static String newCampaignName = "";
    private static String userId = "";
    private static String existingCampaignArn = "";

    private static String putEventTrackerId = "";
    private static String putEventUserId = "";
    private static String putEventItemId = "";
    private static String putEventSessionId = "";

    private static String itemsDatasetArn = "";
    private static String item1Id = "";
    private static String item1PropertyName = "";
    private static String item1PropertyValue = "";
    private static String item2Id = "";
    private static String item2PropertyName = "";
    private static String item2PropertyValue = "";

    private static String usersDatasetArn = "";
    private static String user1Id = "";
    private static String user1PropertyName = "";
    private static String user1PropertyValue = "";

    private static String user2Id = "";
    private static String user2PropertyName = "";
    private static String user2PropertyValue = "";

    private static String filterName = "";
    private static String filterDatasetGroup = "";
    private static String filterExpression = "";

    private static String filterRecCampaignArn = "";
    private static String filterRecUserId = "";
    private static String filterRecFilterArn = "";
    private static String filterRecParameter1Name = "";
    private static String filterRecParameterValue1 = "";
    private static String filterRecParameterValue2 = "";
    private static String filterRecParameter2Name = "";
    private static String filterRecParameter2Value = "";

    private static String batchSolutionVersionArn = "";
    private static String jobName = "";
    private static String s3InputDataSource = "";
    private static String s3DataDestination = "";
    private static String batchServiceRoleArn = "";
    private static String explorationWeight = "";
    private static String explorationItemAgeCutOff = "";

    private static ArrayList<String> rankingItemIds;
    private static String rankingCampaign = "";
    private static String rankingUserId = "";

    private static IngestionMode ingestionMode = IngestionMode.ALL;
    private static String exportJobName = "";
    private static String exportDatasetArn = "";
    private static String exportS3BucketPath = "";
    private static String kmsKeyArn = "";

    private static String eventTrackerName = "";

    private static String updateCampaignArn = "";
    private static String updateCampaignSolutionVersionArn = "";
    private static Integer updateCampaignTps = 1;

    private static String deleteEventTrackerArn = "";

    private static String createSolutionVersionSolutionArn = "";


    // Domain dataset group data members

    private static String createDomainDatasetGroupName = "";
    private static String createDomainDatasetGroupDomain = "";

    private static String createDomainSchemaName = "";
    private static String createDomainSchemaDomain = "";
    private static String createDomainSchemaLocation = "";

    private static String domainSchemaArn = "";
    private static String newDomainDatasetName = "";
    private static String newDomainDatasetType = "";

    private static String domainDatasetImportJobName = "";
    private static String domainImportDatasetArn = "";
    private static String domainS3BucketPath = "";
    private static String domainRoleArn = "";

    private static String createRecommenderName = "";
    private static String createRecommenderRecipeArn = "";
    private static String createRecommenderDatasetGroupArn = "";



    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;

        personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();
        personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();
        personalizeEventsClient = PersonalizeEventsClient.builder()
                .region(region)
                .build();
        try (InputStream input = PersonalizeTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests

//            // CreateDatasetGroup data members
//            newDatasetGroupName = prop.getProperty("newDatasetGroupName");
//
//            // CreateSchema data member
//            schemaName = prop.getProperty("schemaName");
//            schemaLocation = prop.getProperty("schemaLocation");
//
//            // CreateDataset data members
//            schemaArn = prop.getProperty("schemaArn");
//            newDatasetName = prop.getProperty("newDatasetName");
//            newDatasetType = prop.getProperty("datasetType");
//            existingDatasetGroup = prop.getProperty("existingDatasetGroup");
//
//            // CreateDatasetImportJob data members
//            datasetImportJobName = prop.getProperty("datasetImportJobName");
//            importDatasetArn = prop.getProperty("importDatasetArn");
//            s3BucketPath = prop.getProperty("s3BucketPath");
//            roleArn = prop.getProperty("roleArn");
//
//            // CreateSolution and ListSolution data members
//            newSolutionDatasetGroupArn = prop.getProperty("newSolutionDatasetGroupArn");
//            recipeArn = prop.getProperty("recipeArn");
//            newSolutionName = prop.getProperty("newSolutionName");
//
//            // CreateCampaign data members
//            existingSolutionVersionArn = prop.getProperty("existingSolutionVersionArn");
//            newCampaignName = prop.getProperty("campaignName");
//
//            // DescribeCampaign, GetRecommendations, and DeleteCampaign data member
//            existingCampaignArn = prop.getProperty("existingCampaignArn");
//            userId = prop.getProperty("userId");
//
//            // PutEvents data members
//            putEventTrackerId = prop.getProperty("putEventTrackerId");
//            putEventUserId = prop.getProperty("putEventUserId");
//            putEventItemId = prop.getProperty("putEventItemId");
//            putEventSessionId = prop.getProperty("putEventSessionId");
//
//            // PutItems data members
//            itemsDatasetArn = prop.getProperty("itemsDatasetArn");
//            item1Id = prop.getProperty("item1Id");
//            item1PropertyName = prop.getProperty("item1PropertyName");
//            item1PropertyValue = prop.getProperty("item1PropertyValue");
//
//            item2Id = prop.getProperty("item2Id");
//            item2PropertyName = prop.getProperty("item2PropertyName");
//            item2PropertyValue = prop.getProperty("item2PropertyValue");
//
//            // PutUsers data members
//            usersDatasetArn = prop.getProperty("usersDatasetArn");
//            user1Id = prop.getProperty("user1Id");
//            user1PropertyName = prop.getProperty("user1PropertyName");
//            user1PropertyValue = prop.getProperty("user1PropertyValue");
//
//            user2Id = prop.getProperty("user2Id");
//            user2PropertyName = prop.getProperty("user2PropertyName");
//            user2PropertyValue = prop.getProperty("user2PropertyValue");
//
//            // CreateFilter data members
//            filterName = prop.getProperty("filterName");
//            filterDatasetGroup = prop.getProperty("filterDatasetGroupArn");
//            filterExpression = prop.getProperty("filterExpression");
//
//            // FilterRecommendation data members
//            filterRecCampaignArn = prop.getProperty("filterRecCampaignArn");
//            filterRecUserId = prop.getProperty("filterRecUserId");
//            filterRecFilterArn = prop.getProperty("filterRecFilterArn");
//            filterRecParameter1Name = prop.getProperty("filterRecParameter1Name");
//            filterRecParameterValue1 = prop.getProperty("filterRecParameterValue1");
//            filterRecParameterValue2 = prop.getProperty("filterRecParameterValue2");
//            filterRecParameter2Name = prop.getProperty("filterRecParameter2Name");
//            filterRecParameter2Value = prop.getProperty("filterRecParameter2Value");
//
//            // CreateBatchInferenceJob data members
//            batchSolutionVersionArn = prop.getProperty("batchSolutionVersionArn");
//            jobName = prop.getProperty("jobName");
//            s3InputDataSource = prop.getProperty("s3InputDataSource");
//            s3DataDestination = prop.getProperty("s3DataDestination");
//            batchServiceRoleArn = prop.getProperty("batchServiceRoleArn");
//            explorationWeight = prop.getProperty("explorationWeight");
//            explorationItemAgeCutOff = prop.getProperty("explorationItemAgeCutOff");
//
//            // GetPersonalizeRanking data members
//            rankingItemIds = new ArrayList<>(Arrays.asList(prop.getProperty("rankingItemIds")
//                    .split("\\s*,\\s*")));
//            rankingCampaign = prop.getProperty("rankingCampaign");
//            rankingUserId = prop.getProperty("rankingUserId");
//
//            // CreateDatasetExportJob members
//
//            if (prop.getProperty("ingestionMode").equalsIgnoreCase("put")) {
//                ingestionMode = IngestionMode.PUT;
//            } else if (prop.getProperty("ingestionMode").equalsIgnoreCase("bulk")) {
//                ingestionMode = IngestionMode.BULK;
//            }
//            exportJobName = prop.getProperty("exportJobName");
//            exportDatasetArn = prop.getProperty("exportDatasetArn");
//            exportS3BucketPath = prop.getProperty("exportS3BucketPath");
//            kmsKeyArn = prop.getProperty("kmsKeyArn");
//
//            // CreateEventTracker members
//            eventTrackerName = prop.getProperty("eventTrackerName");
//
//            // UpdateCampaign members
//            updateCampaignArn = prop.getProperty("updateCampaignArn");
//            updateCampaignSolutionVersionArn = prop.getProperty("updateCampaignSolutionVersionArn");
//            updateCampaignTps = Integer.parseInt(prop.getProperty("updateCampaignTps"));
//
//            // DeleteEventTracker members
//            deleteEventTrackerArn = prop.getProperty("deleteEventTrackerArn");
//
//            // CreateSolutionVersion members
//            createSolutionVersionSolutionArn = prop.getProperty("createSolutionVersionSolutionArn");
//
            //******** Domain dataset group members *************//

            createDomainDatasetGroupName = prop.getProperty("createDomainDatasetGroupName");
            createDomainDatasetGroupDomain = prop.getProperty("createDomainDatasetGroupDomain");

            createDomainSchemaName = prop.getProperty("createDomainSchemaName");
            createDomainSchemaDomain = prop.getProperty("createDomainSchemaDomain");
            createDomainSchemaLocation = prop.getProperty("createDomainSchemaLocation");

            domainSchemaArn = prop.getProperty("domainSchemaArn");
            newDomainDatasetName = prop.getProperty("newDomainDatasetName");
            newDomainDatasetType = prop.getProperty("newDomainDatasetType");

            domainDatasetImportJobName = prop.getProperty("domainDatasetImportJobName");
            domainImportDatasetArn = prop.getProperty("");
            domainS3BucketPath = prop.getProperty("");
            domainRoleArn = prop.getProperty("");
            createRecommenderName = prop.getProperty("createRecommenderName");
            createRecommenderRecipeArn = prop.getProperty("createRecommenderRecipeArn");
            createRecommenderDatasetGroupArn = prop.getProperty("createRecommenderDatasetGroupArn");


//


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(personalizeRuntimeClient);
        assertNotNull(personalizeClient);
        assertNotNull(personalizeEventsClient);
        System.out.println("Initialize clients test passed");
    }

    @Test
    @Order(2)
    public void CreateDatasetGroup() {
        String newDatasetGroupArn = CreateDatasetGroup.createDatasetGroup(personalizeClient, newDatasetGroupName);
        assertFalse(newDatasetGroupArn.isEmpty());
        System.out.println("CreateDatasetGroup test passed");
    }

    @Test
    @Order(3)
    public void CreateDataset() {
        String newDatasetArn = CreateDataset.createDataset(personalizeClient,
                newDatasetName, existingDatasetGroup, newDatasetType, schemaArn);
        assertFalse(newDatasetArn.isEmpty());
        System.out.println("CreateDataset test passed");
    }

    @Test
    @Order(4)
    public void CreateDatasetImportJob() {
        String datasetImportJobArn = CreateDatasetImportJob.createPersonalizeDatasetImportJob(personalizeClient,
                datasetImportJobName, importDatasetArn, s3BucketPath, roleArn);
        assertFalse(datasetImportJobArn.isEmpty());
        System.out.println("CreateDatasetImportJob test passed");
    }


    @Test
    @Order(5)
    public void CreateSolution() {
        solutionArn = CreateSolution.createPersonalizeSolution(personalizeClient, newSolutionDatasetGroupArn, newSolutionName, recipeArn);
        assertFalse(solutionArn.isEmpty());
        System.out.println("CreationSolution test passed");
    }

    @Test
    @Order(6)
    public void ListSolutions() {

        ListSolutions.listAllSolutions(personalizeClient, newSolutionDatasetGroupArn);
        System.out.println("List Solutions test passed");
    }

    @Test
    @Order(7)
    public void DescribeSolution() {

        DescribeSolution.describeSpecificSolution(personalizeClient, solutionArn);
        System.out.println("DescribeSolution test passed");
    }

    @Test
    @Order(8)
    public void CreateCampaign() {
        String campaignArn = CreateCampaign.createPersonalCampaign(personalizeClient, existingSolutionVersionArn, newCampaignName);
        assertFalse(campaignArn.isEmpty());
        System.out.println("CreateCampaign test passed");
    }

    @Test
    @Order(9)
    public void DescribeCampaign() {
        DescribeCampaign.describeSpecificCampaign(personalizeClient, existingCampaignArn);
        System.out.println("DescribeCampaign test passed");
    }

    @Test
    @Order(10)
    public void ListCampaigns() {

        ListCampaigns.listAllCampaigns(personalizeClient, solutionArn);
        System.out.println("ListCampaigns test passed");
    }

    @Test
    @Order(11)
    public void DescribeRecipe() {
        DescribeRecipe.describeSpecificRecipe(personalizeClient, recipeArn);
        System.out.println("DescribeRecipe test passed");
    }

    @Test
    @Order(12)
    public void ListRecipes() {
        ListRecipes.listAllRecipes(personalizeClient);
        System.out.println("ListRecipes passed");
    }

    @Test
    @Order(13)
    public void ListDatasetGroups() {
        ListDatasetGroups.listDSGroups(personalizeClient);
        System.out.println("ListDatasetGroups test passed");
    }

    @Test
    @Order(14)
    public void DeleteSolution() {
        DeleteSolution.deleteGivenSolution(personalizeClient, solutionArn);
        System.out.println("DeleteSolution test passed");
    }

    @Test
    @Order(15)
    public void GetRecommendations() {
        GetRecommendations.getRecs(personalizeRuntimeClient, existingCampaignArn, userId);
        System.out.println("GetRecommendations test passed");
    }

    @Test
    @Order(16)
    public void PutEvents() {
        int responseCode = PutEvents.putEvents(personalizeEventsClient, putEventTrackerId, putEventUserId,
                putEventItemId, putEventSessionId);
        assertEquals(responseCode, 200);
        System.out.println("PutEvents test passed");
    }

    @Test
    @Order(17)
    public void PutItems() {
        int responseCode = PutItems.putItems(personalizeEventsClient,
                itemsDatasetArn, item1Id, item1PropertyName, item1PropertyValue, item2Id,
                item2PropertyName, item2PropertyValue);
        assertEquals(responseCode, 200);
        System.out.println("PutItems test passed");
    }

    @Test
    @Order(18)
    public void PutUsers() {
        int responseCode = PutUsers.putUsers(personalizeEventsClient,
                usersDatasetArn, user1Id, user1PropertyName, user1PropertyValue,
                user2Id, user2PropertyName, user2PropertyValue);
        assertEquals(responseCode, 200);
        System.out.println("PutUsers test passed");
    }

    @Test
    @Order(19)
    public void DeleteCampaign() {
        DeleteCampaign.deleteSpecificCampaign(personalizeClient, existingCampaignArn);
        System.out.println("DeleteCampaign test passed");
    }

    @Test
    @Order(20)
    public void CreateFilter() {
        String filterArn = CreateFilter.createFilter(personalizeClient, filterName, filterDatasetGroup, filterExpression);
        assertFalse(filterArn.isEmpty());
        System.out.println("CreateFilter test passed");
    }

    @Test
    @Order(21)
    public void FilterRecommendations() {
        FilterRecommendations.getFilteredRecs(personalizeRuntimeClient, filterRecCampaignArn, filterRecUserId,
                filterRecFilterArn, filterRecParameter1Name, filterRecParameterValue1, filterRecParameterValue2, filterRecParameter2Name,
                filterRecParameter2Value);
        System.out.println("Filter recommendations passed");
    }

    @Test
    @Order(22)
    public void CreateBatchInferenceJob() {
        String batchArn = CreateBatchInferenceJob.createPersonalizeBatchInferenceJob(personalizeClient, batchSolutionVersionArn,
                jobName, s3InputDataSource, s3DataDestination, batchServiceRoleArn, explorationWeight, explorationItemAgeCutOff);
        assertFalse(batchArn.isEmpty());
        System.out.println("CreateBatchInferenceJob test passed");
    }

    @Test
    @Order(23)
    public void GetPersonalizedRanking() {
        List<PredictedItem> rankedItems = GetPersonalizedRanking.getRankedRecs(personalizeRuntimeClient,
                rankingCampaign, rankingUserId, rankingItemIds);
        assertFalse(rankedItems.isEmpty());
        System.out.println("CreateBatchInferenceJob test passed");
    }

    @Test
    @Order(24)
    public void CreateDatasetExportJob() {
        String exportStatus = CreateDatasetExportJob.createDatasetExportJob(personalizeClient, exportJobName,
                exportDatasetArn, ingestionMode, roleArn, exportS3BucketPath, kmsKeyArn);
        assertEquals("ACTIVE", exportStatus);
        System.out.println("CreateDatasetExportJob test passed");
    }

    @Test
    @Order(25)
    public void CreateEventTracker() {
        String eventTrackerId = CreateEventTracker.createEventTracker(personalizeClient, eventTrackerName, existingDatasetGroup);
        assertFalse(eventTrackerId.isEmpty());
        System.out.println("CreateEventTracker test passed");
    }

    @Test
    @Order(26)
    public void CreateSchema() {
        String schemaArn = CreateSchema.createSchema(personalizeClient, schemaName, schemaLocation);
        assertFalse(schemaArn.isEmpty());
        System.out.println("CreateSchema test passed");
    }
    @Test
    @Order(27)
    public void UpdateCampaign() {
        String status = UpdateCampaign.updateCampaign(personalizeClient, updateCampaignArn,
                updateCampaignSolutionVersionArn, updateCampaignTps);
        assertFalse(status.isEmpty());
        System.out.println("UpdateCampaign test passed");
    }
    @Test
    @Order(28)
    public void DeleteEventTracker() {
        DeleteEventTracker.deleteEventTracker(personalizeClient, deleteEventTrackerArn);
        System.out.println("DeleteEventTracker test passed");
    }
    @Test
    @Order(29)
    public void CreateSolutionVersion() {
        String solutionVersionArn = CreateSolutionVersion.createPersonalizeSolutionVersion(personalizeClient, createSolutionVersionSolutionArn);
        assertFalse(solutionVersionArn.isEmpty());
        System.out.println("Create Solution Version test passed");
    }
    @Test
    @Order(30)
    public void CreateDomainDatasetGroup() {
        String datasetGroupArn = CreateDomainDatasetGroup.createDomainDatasetGroup(personalizeClient,
                createDomainDatasetGroupName, createDomainDatasetGroupDomain);
        assertFalse(datasetGroupArn.isEmpty());
        System.out.println("CreateDomainDatasetGroup test passed");
    }
    @Test
    @Order(31)
    public void CreateDomainSchema() {
        String schemaArn = CreateDomainSchema.createDomainSchema(personalizeClient,
                createDomainSchemaName, createDomainSchemaDomain, createDomainSchemaLocation);
        assertFalse(schemaArn.isEmpty());
        System.out.println("CreateDomainDatasetGroup test passed");
    }
    @Test
    @Order(32)
    public void CreateDomainResources() {

    }



    @Test
    @Order(33)
    public void CreateRecommender() {
        String recommenderArn = CreateRecommender.createRecommender(personalizeClient,
                createRecommenderName, createRecommenderDatasetGroupArn, createRecommenderRecipeArn);
        assertFalse(recommenderArn.isEmpty());
        System.out.println("CreateRecommender test passed");
    }



}
