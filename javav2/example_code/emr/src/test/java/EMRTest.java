import aws.example.emr.*;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emr.EmrClient;
import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EMRTest {

    private static EmrClient emrClient;
    private static String jar = "";
    private static String myClass = "" ;
    private static String keys = "" ;
    private static String logUri = "" ;
    private static String name = "" ;
    private static String jobFlowId = "";
    private static String existingClusterId = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        Region region = Region.US_WEST_2;
        emrClient = EmrClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = EMRTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            jar = prop.getProperty("jar");
            myClass = prop.getProperty("myClass");
            keys = prop.getProperty("keys");
            logUri = prop.getProperty("logUri");
            name = prop.getProperty("name");
            existingClusterId= prop.getProperty("existingClusterId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(emrClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void createClusterTest() {
        jobFlowId =  CreateCluster.createAppCluster(emrClient, jar, myClass, keys, logUri, name);
        assertTrue(!jobFlowId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void describeClusterTest() {
        DescribeCluster.describeMyCluster(emrClient, existingClusterId);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void listClusterTest() {
        ListClusters.listAllClusters(emrClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void createEmrFleetTest() {
        CreateEmrFleet.createFleet(emrClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void addStepsTest() {
        AddSteps.addNewStep(emrClient, jobFlowId, jar, myClass);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void createSparkClusterTest(){
        CreateSparkCluster.createCluster(emrClient, jar, myClass, keys, logUri, name);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void createHiveClusterTest() {
        CreateHiveCluster.createCluster(emrClient, jar, myClass, keys, logUri, name);
        System.out.println("Test 8 passed");

    }

    @Test
    @Order(9)
    public void customEmrfsMaterialsTest(){
        CustomEmrfsMaterials.createEmrfsCluster(emrClient, jar, myClass, keys, logUri, name);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void terminateJobFlowTest(){

        TerminateJobFlow.terminateFlow(emrClient, existingClusterId);
        System.out.println("Test 10 passed");
    }


}
