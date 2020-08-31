import com.example.personalize.*;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;

import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonalizeServiceIntegrationTest {

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

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_EAST_1;
        personalizeRuntimeClient = PersonalizeRuntimeClient.builder()
                .region(region)
                .build();

        personalizeClient = PersonalizeClient.builder()
                .region(region)
                .build();


        try (InputStream input = PersonalizeServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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
            solutionName = prop.getProperty("solutionName");
            campaignArn = prop.getProperty("campaignArn");
            userId = prop.getProperty("userId");
            campaignName= prop.getProperty("campaignName");
             existingSolutionArn= prop.getProperty("existingSolutionArn");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
   }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(personalizeRuntimeClient);
        assertNotNull(personalizeClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
     public void CreateSolution() {
     solutionArn = CreateSolution.createPersonalizeSolution(personalizeClient, datasetGroupArn, solutionName, recipeArn);
     assertTrue(!solutionArn.isEmpty());
     System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void ListSolutions() {

        ListSolutions.listAllSolutions(personalizeClient, datasetGroupArn);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DescribeSolution() {

        DescribeSolution.describeSpecificSolution(personalizeClient, solutionArn);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CreateCampaign() {
        CreateCampaign.createPersonalCompaign(personalizeClient, solutionVersionArn, campaignName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DescribeCampaign() {
        DescribeCampaign.describeSpecificCampaign(personalizeClient, campaignArn);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListCampaigns() {

        ListCampaigns.listAllCampaigns(personalizeClient, solutionArn);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DescribeRecipe() {
        DescribeRecipe.describeSpecificRecipe(personalizeClient, recipeArn);
        System.out.println("Test 8 passed");
   }

    @Test
    @Order(9)
   public void ListRecipes() {
       ListRecipes.listAllRecipes(personalizeClient);
        System.out.println("Test 9 passed");
   }

    @Test
    @Order(10)
   public void ListDatasetGroups() {
       ListDatasetGroups.listDSGroups(personalizeClient);
        System.out.println("Test 10 passed");
   }

    @Test
    @Order(11)
   public void DeleteSolution() {
       DeleteSolution.deleteGivenSolution(personalizeClient,solutionArn);
       System.out.println("Test 11 passed");
   }

    @Test
    @Order(12)
   public void GetRecommendations() {
       GetRecommendations.getRecs(personalizeRuntimeClient,campaignArn, userId);
        System.out.println("Test 12 passed");
   }
}
