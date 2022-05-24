import com.example.deploy.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.codedeploy.CodeDeployClient;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CodeDeployTest {

    private static CodeDeployClient deployClient;
    private static String appName = "";
    private static String existingApp = "";
    private static String existingDeployment = "";
    private static String bucketName = "";
    private static String key = "";
    private static String bundleType = "";
    private static String newDeploymentGroupName = "";
    private static String deploymentId="" ;
    private static String serviceRoleArn="" ;
    private static String tagKey="";
    private static String tagValue="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        deployClient = CodeDeployClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = CodeDeployTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            appName = prop.getProperty("appName");
            existingApp = prop.getProperty("existingApp");
            existingDeployment = prop.getProperty("existingDeployment");
            bucketName = prop.getProperty("bucketName");
            key = prop.getProperty("key");
            bundleType = prop.getProperty("bundleType");
            newDeploymentGroupName  = prop.getProperty("newDeploymentGroupName");
            serviceRoleArn = prop.getProperty("serviceRoleArn");
            tagKey = prop.getProperty("tagKey");
            tagValue = prop.getProperty("tagValue");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(deployClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateApplication() {
        CreateApplication.createApp(deployClient, appName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void ListApplications() {
        ListApplications.listApps(deployClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DeployApplication() {
        deploymentId = DeployApplication.createAppDeployment(deployClient, existingApp, bucketName, bundleType, key, existingDeployment);
        assertTrue(!deploymentId.isEmpty());
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CreateDeploymentGroup() {
        CreateDeploymentGroup.createNewDeploymentGroup(deployClient, newDeploymentGroupName, appName, serviceRoleArn, tagKey, tagValue );
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListDeploymentGroups() {
        ListDeploymentGroups.listDeployGroups(deployClient, appName);
        System.out.println("Test 6 passed");
   }

    @Test
    @Order(7)
   public void GetDeployment(){
       GetDeployment.getSpecificDeployment(deployClient,deploymentId);
        System.out.println("Test 7 passed");
   }

    @Test
    @Order(8)
   public void DeleteDeploymentGroup() {
       DeleteDeploymentGroup.delDeploymentGroup(deployClient, appName, newDeploymentGroupName);
        System.out.println("Test 8 passed");
   }

    @Test
    @Order(9)
   public void DeleteApplication() {
       DeleteApplication.delApplication(deployClient, appName);
       System.out.println("Test 9 passed");

   }
}