import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import com.example.cloudfront.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudFrontTest {

    private static CloudFrontClient cloudFrontClient ;
    private static Region region;
    private static String functionName = "";
    private static String filePath = "";
    private static String funcARN = "";
    private static String eTagVal = "";
    private static String id = "";


    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.AWS_GLOBAL;
        cloudFrontClient = CloudFrontClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        try (InputStream input = CloudFrontTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            functionName = prop.getProperty("functionName");
            filePath= prop.getProperty("filePath");
            id = prop.getProperty("id");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(cloudFrontClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateFunction() {
        funcARN =  CreateFunction.createNewFunction(cloudFrontClient, functionName, filePath);
        assertTrue(!funcARN.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeFunction() {
        eTagVal = DescribeFunction.describeSinFunction(cloudFrontClient, functionName);
        assertTrue(!eTagVal.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListFunctions(){
        ListFunctions.listAllFunctions(cloudFrontClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
   public void GetDistrubutions() {
        GetDistrubutions.getCFDistrubutions(cloudFrontClient);
        System.out.println("Test 5 passed");
   }


    @Test
    @Order(6)
   public void ModifyDistrution() {

        ModifyDistribution.modDistribution(cloudFrontClient, id);
        System.out.println("Test 6 passed");
   }

    @Test
    @Order(7)
   public void DeleteFunction(){

       DeleteFunction.deleteSpecificFunction(cloudFrontClient, functionName, eTagVal);
       System.out.println("Test 7 passed");
    }
}

