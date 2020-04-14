import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
import java.io.*;
import com.example.pinpoint.*;

import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonPinpointServiceIntegrationTest {

    private static PinpointClient pinpoint;
    private static Region region;
    private static String appName = "";
    private static String appId = ""; //gets set in test 2
    private static String endpointId2 = ""; //gets set in test 3
    private static String bucket = "";
    private static String path= "";
    private static String roleArn= "";
    private static String segmentId= ""; // set in a test 7

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_EAST_1;
        pinpoint = PinpointClient.builder().region(region).build();
        try (InputStream input = AmazonPinpointServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            appName = prop.getProperty("appName");
            bucket= prop.getProperty("bucket");
            path= prop.getProperty("path");
            roleArn= prop.getProperty("roleArn");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSPinpointService_thenNotNull() {
        assertNotNull(pinpoint);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateApp() {

        try {
            appId = CreateApp.createApplication(pinpoint, appName);
            assertTrue(!appId.isEmpty());

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void CreateEndpoint()
    {
       try {

           EndpointResponse response = CreateEndpoint.createEndpoint(pinpoint, appId);
           endpointId2 = response.id() ;
           assertTrue(!endpointId2.isEmpty());

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void DeleteEndpoint() {

        try {

            DeleteEndpoint.deletePinEncpoint(pinpoint, appId, endpointId2 );
           } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void SendMessage() {

        try {
        SendMessage.sendSMSMessage(pinpoint, "Hello, this is a Pinpoint test");

        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        System.out.println("Test 5 passed");
    }


    @Test
    @Order(6)
    public void ImportSegments() {

        try {
            ImportSegment.createImportSegment(pinpoint, appId, bucket, path, roleArn);
        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void ListSegments() {

        try {
            ListSegments.listSegs(pinpoint, appId);
        } catch ( PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void CreateSegment() {

        try {
            SegmentResponse createSegmentResult =  CreateSegment.createSegment(pinpoint, appId);
            segmentId =  createSegmentResult.id();
            assertTrue(!segmentId.isEmpty());
        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 8 passed");
        }

    @Test
    @Order(9)
    public void CreateCampaign() {

      try {
           CreateCampaign.createPinCampaign(pinpoint, appId, segmentId );
          } catch (PinpointException e) {
              System.err.println(e.awsErrorDetails().errorMessage());
              System.exit(1);
          }
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void DeleteApp() {
        try {
            DeleteApp.deletePinApp(pinpoint, appId);
        } catch (PinpointException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Test 10 passed");
    }
}
