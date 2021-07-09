import com.example.personalize.*;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;

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
    private static String newDatasetGroupArn = "";

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

            // CreateDatasetGroup data members
            newDatasetGroupName = prop.getProperty("newDatasetGroupName");

            // CreateDataset data members
            schemaArn = prop.getProperty("schemaArn");
            newDatasetName = prop.getProperty("newDatasetName");
            newDatasetType = prop.getProperty("datasetType");
            existingDatasetGroup = prop.getProperty("existingDatasetGroup");

            // CreateDatasetImportJob data members
            datasetImportJobName = prop.getProperty("datasetImportJobName");
            importDatasetArn = prop.getProperty("importDatasetArn");
            s3BucketPath = prop.getProperty("s3BucketPath");
            roleArn = prop.getProperty("roleArn");

            // CreateSolution and ListSolution data members
            newSolutionDatasetGroupArn = prop.getProperty("newSolutionDatasetGroupArn");
            recipeArn = prop.getProperty("recipeArn");
            newSolutionName = prop.getProperty("newSolutionName");

            // CreateCampaign data members
            existingSolutionVersionArn = prop.getProperty("existingSolutionVersionArn");
            newCampaignName = prop.getProperty("campaignName");

            // DescribeCampaign, GetRecommendations, and DeleteCampaign data member
            existingCampaignArn = prop.getProperty("existingCampaignArn");
            userId = prop.getProperty("userId");

            // PutEvents data members
            putEventTrackerId = prop.getProperty("putEventTrackerId");
            putEventUserId = prop.getProperty("putEventUserId");
            putEventItemId = prop.getProperty("putEventItemId");
            putEventSessionId = prop.getProperty("putEventSessionId");


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
        newDatasetGroupArn = CreateDatasetGroup.createDatasetGroup(personalizeClient, newDatasetGroupName);
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
        CreateCampaign.createPersonalCompaign(personalizeClient, existingSolutionVersionArn, newCampaignName);
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
    public void DeleteCampaign() {
        DeleteCampaign.deleteSpecificCampaign(personalizeClient, existingCampaignArn);
        System.out.println("DeleteCampaign test passed");
    }
}
