import com.example.personalize.CreateDataset;
import com.example.personalize.CreateDatasetImportJob;
import com.example.personalize.CreateDomainDatasetGroup;
import com.example.personalize.CreateDomainSchema;
import com.example.personalize.CreateRecommender;
import com.example.personalize.GetRecommendationsFromRecommender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.personalize.PersonalizeClient;
import software.amazon.awssdk.services.personalizeevents.PersonalizeEventsClient;
import software.amazon.awssdk.services.personalizeruntime.PersonalizeRuntimeClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonalizeDomainTest {

    private static PersonalizeRuntimeClient personalizeRuntimeClient;
    private static PersonalizeClient personalizeClient;
    private static PersonalizeEventsClient personalizeEventsClient;


    private static String createDomainDatasetGroupName = "";
    private static String createDomainDatasetGroupDomain = "";

    private static String createDomainSchemaName = "";
    private static String createDomainSchemaDomain = "";
    private static String createDomainSchemaLocation = "";

    private static String newDatasetSchemaArn = "";
    private static String newDatasetName = "";
    private static String newDatasetType = "";
    private static String newDatasetDestinationDatasetGroupArn = "";


    private static String importJobDatasetArn = "";
    private static String domainDatasetImportJobName = "";
    private static String domainS3BucketPath = "";
    private static String domainRoleArn = "";

    private static String createRecommenderName = "";
    private static String createRecommenderRecipeArn = "";
    private static String createRecommenderDatasetGroupArn = "";

    private static String getRecommendationsRecommenderArn = "";
    private static String getRecommendationsUserId = "";

    @BeforeAll
    public static void setUp() throws IOException {

        //Change to the region where your Amazon Personalize resources are located.
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
        try (InputStream input = PersonalizeDomainTest.class.getClassLoader().getResourceAsStream("domain-dsg-config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Unable to find domain-dsg-config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests

            createDomainDatasetGroupName = prop.getProperty("createDomainDatasetGroupName");
            createDomainDatasetGroupDomain = prop.getProperty("createDomainDatasetGroupDomain");

            createDomainSchemaName = prop.getProperty("createDomainSchemaName");
            createDomainSchemaDomain = prop.getProperty("createDomainSchemaDomain");
            createDomainSchemaLocation = prop.getProperty("createDomainSchemaLocation");

            newDatasetName = prop.getProperty("newDatasetName");
            newDatasetType = prop.getProperty("newDatasetType");
            newDatasetSchemaArn = prop.getProperty("newDatasetSchemaArn");
            newDatasetDestinationDatasetGroupArn = prop.getProperty("newDatasetDestinationDatasetGroupArn");

            domainDatasetImportJobName = prop.getProperty("domainDatasetImportJobName");
            domainS3BucketPath = prop.getProperty("domainS3BucketPath");
            domainRoleArn = prop.getProperty("domainRoleArn");
            importJobDatasetArn = prop.getProperty("importJobDatasetArn");

            createRecommenderName = prop.getProperty("createRecommenderName");
            createRecommenderRecipeArn = prop.getProperty("createRecommenderRecipeArn");
            createRecommenderDatasetGroupArn = prop.getProperty("recommenderDatasetGroupArn");

            getRecommendationsUserId = prop.getProperty("getRecommendationsUserId");
            getRecommendationsRecommenderArn = prop.getProperty("getRecommendationsRecommenderArn");

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
    public void CreateDomainDatasetGroup() {
        String domainDatasetGroupArn = CreateDomainDatasetGroup.createDomainDatasetGroup(personalizeClient,
                createDomainDatasetGroupName, createDomainDatasetGroupDomain);
        assertFalse(domainDatasetGroupArn.isEmpty());
        System.out.println("CreateDomainDatasetGroup test passed");
    }
    @Test
    @Order(3)
    public void CreateDomainSchema() {
        String domainSchemaArn = CreateDomainSchema.createDomainSchema(personalizeClient,
                createDomainSchemaName, createDomainSchemaDomain, createDomainSchemaLocation);
        assertFalse(domainSchemaArn.isEmpty());
        System.out.println("CreateDomainSchema test passed");
    }
    @Test
    @Order(4)
    public void CreateDomainDataset() {
        String datasetArn = CreateDataset.createDataset(personalizeClient,
                newDatasetName, newDatasetDestinationDatasetGroupArn, newDatasetType, newDatasetSchemaArn);
        assertFalse(datasetArn.isEmpty());
        System.out.println("CreateDomainDataset test passed");
    }
    @Test
    @Order(5)
    public void CreateDatasetImportJob() {
        String datasetImportJobArn = CreateDatasetImportJob.createPersonalizeDatasetImportJob(personalizeClient,
                domainDatasetImportJobName, importJobDatasetArn, domainS3BucketPath, domainRoleArn);
        assertFalse(datasetImportJobArn.isEmpty());
        System.out.println("CreateDatasetImportJob test passed");
    }

    @Test
    @Order(6)
    public void CreateRecommender() {
        String recommenderArn = CreateRecommender.createRecommender(personalizeClient,
                createRecommenderName, createRecommenderDatasetGroupArn, createRecommenderRecipeArn);
        assertFalse(recommenderArn.isEmpty());
        System.out.println("CreateRecommender test passed");
    }
    @Test
    @Order(7)
    public void GetRecommendations() {
        GetRecommendationsFromRecommender.getRecs(personalizeRuntimeClient, getRecommendationsRecommenderArn, getRecommendationsUserId);
        System.out.println("GetRecommendations test passed");
    }




}
