import com.example.glue.*;
import software.amazon.awssdk.services.glue.GlueClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GlueTest {

    private static GlueClient glueClient;
    private static String crawlerName="";
    private static String cron="";
    private static String s3Path="";
    private static String IAM="";
    private static String databaseName="";
    private static String tableName="";
    private static String text="";
    private static String existingDatabaseName="";
    private static String existingCrawlerName="";
    private static String jobNameSc="";
    private static String s3PathSc="";
    private static String dbNameSc="";
    private static String crawlerNameSc="";
    private static String scriptLocationSc="";
    private static String locationUri="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        glueClient = GlueClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        
        try (InputStream input = GlueTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            crawlerName = prop.getProperty("crawlerName");
            s3Path = prop.getProperty("s3Path");
            cron = prop.getProperty("cron");
            IAM = prop.getProperty("IAM");
            databaseName = prop.getProperty("databaseName");
            tableName = prop.getProperty("tableName");
            text = prop.getProperty("text");
            existingDatabaseName = prop.getProperty("existingDatabaseName");
            existingCrawlerName = prop.getProperty("existingCrawlerName");
            jobNameSc = prop.getProperty("jobNameSc");
            s3PathSc = prop.getProperty("s3PathSc");
            dbNameSc = prop.getProperty("dbNameSc");
            crawlerNameSc = prop.getProperty("crawlerNameSc");
            scriptLocationSc = prop.getProperty("scriptLocationSc");
            locationUri = prop.getProperty("locationUri");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(glueClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateCrawler(){
        CreateCrawler.createGlueCrawler(glueClient, IAM, s3Path, cron, databaseName, crawlerName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void GetCrawler() {
        GetCrawler.getSpecificCrawler(glueClient, crawlerName);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetCrawlers() {
        GetCrawlers.getAllCrawlers(glueClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void StartCrawler() {
        StartCrawler.startSpecificCrawler(glueClient, crawlerName);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void GetDatabase() {
        GetDatabase.getSpecificDatabase(glueClient, existingDatabaseName);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void GetDatabases() {
        GetDatabases.getAllDatabases(glueClient);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void GetTable() {
        GetTable.getGlueTable(glueClient, existingDatabaseName, tableName);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void SearchTables() {
        SearchTables.searchGlueTable(glueClient, text);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void ListWorkflows() {
        ListWorkflows.listAllWorkflows(glueClient);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(11)
    public void DeleteCrawler() {
        DeleteCrawler.deleteSpecificCrawler(glueClient, existingCrawlerName);
        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void ScenarioTest() throws InterruptedException {

        GlueScenario.createDatabase(glueClient, dbNameSc, locationUri);
        GlueScenario.createGlueCrawler(glueClient, IAM, s3Path, cron, dbNameSc, crawlerNameSc);
        GlueScenario.getSpecificCrawler(glueClient, crawlerNameSc);
        GlueScenario.startSpecificCrawler(glueClient, crawlerNameSc);
        GlueScenario.getSpecificDatabase(glueClient, dbNameSc);
        GlueScenario.getGlueTables(glueClient, dbNameSc);
        GlueScenario.createJob(glueClient, jobNameSc, IAM, scriptLocationSc);
        GlueScenario.startJob(glueClient, jobNameSc);
        GlueScenario.getAllJobs(glueClient);
        GlueScenario.getJobRuns(glueClient, jobNameSc);
        GlueScenario.deleteJob(glueClient, jobNameSc);
        System.out.println("*** Wait for 5 MIN so the "+crawlerNameSc +" has stopped");
        TimeUnit.MINUTES.sleep(5);
        GlueScenario.deleteDatabase(glueClient, dbNameSc);
        GlueScenario.deleteSpecificCrawler(glueClient, crawlerNameSc);
    }
}
