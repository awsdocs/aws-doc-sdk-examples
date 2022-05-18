import com.example.cloudtrail.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudTrailTest {

private static CloudTrailClient cloudTrailClient ;
private static String trailName = "";
    private static String s3BucketName = "";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        cloudTrailClient = CloudTrailClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = CloudTrailTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            trailName = prop.getProperty("trailName");
            s3BucketName = prop.getProperty("s3BucketName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(cloudTrailClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateTrail() {
        CreateTrail.createNewTrail(cloudTrailClient, trailName, s3BucketName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void PutEventSelectors() {
        PutEventSelectors.setSelector(cloudTrailClient, trailName);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void GetEventSelectors() {
        GetEventSelectors.getSelectors(cloudTrailClient, trailName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void LookupEvents() {
        LookupEvents.lookupAllEvents(cloudTrailClient);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DescribeTrails() {
        DescribeTrails.describeSpecificTrails(cloudTrailClient, trailName);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void GetTrailLoggingTime() {
        GetTrailLoggingTime.getLogTime(cloudTrailClient, trailName);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void StartLogging() {

        StartLogging.startLog(cloudTrailClient, trailName);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void StopLogging() {

        StartLogging.stopLog(cloudTrailClient, trailName);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void DeleteTrail() {
        DeleteTrail.deleteSpecificTrail(cloudTrailClient, trailName);
        System.out.println("Test 10 passed");
    }
}
