import com.example.cloudformation.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;
import software.amazon.awssdk.regions.Region;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudFormationTest {

    private static  CloudFormationClient cfClient;
    private static String stackName = "";
    private static String roleARN = "";
    private static String location = "";
    private static String key = "";
    private static String value = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        cfClient = CloudFormationClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = CloudFormationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            stackName = prop.getProperty("stackName");
            roleARN = prop.getProperty("roleARN");
            location = prop.getProperty("location");
            key = prop.getProperty("key");
            value = prop.getProperty("value");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(cfClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateStack() {
        CreateStack.createCFStack(cfClient, stackName, roleARN, location, key, value);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeStacks() {
        DescribeStacks.describeAllStacks(cfClient);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetTemplate() {
        GetTemplate.getSpecificTemplate(cfClient, stackName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void DeleteStack(){
        DeleteStack.deleteSpecificTemplate(cfClient, stackName);
        System.out.println("Test 5 passed");
    }
 }
