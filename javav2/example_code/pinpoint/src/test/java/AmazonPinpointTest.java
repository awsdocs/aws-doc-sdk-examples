import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.pinpoint.PinpointClient;
import software.amazon.awssdk.services.pinpoint.model.*;
import java.io.*;
import com.example.pinpoint.*;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.pinpointsmsvoice.PinpointSmsVoiceClient;

import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonPinpointTest {

    private static PinpointClient pinpoint;
    private static PinpointSmsVoiceClient voiceClient;
    private static S3Client s3Client;
    private static Region region;
    private static String appName = "";
    private static String appId = ""; //gets set in test 2
    private static String endpointId2 = ""; //gets set in test 3
    private static String bucket = "";
    private static String path= "";
    private static String roleArn= "";
    private static String segmentId= ""; // set in a test 7
    private static String userId = "";
    private static String s3BucketName = "";
    private static String iamExportRoleArn = "";
    private static String existingApplicationId = "";
    private static String filePath = "";
    private static String subject = "";
    private static String senderAddress = "";
    private static String toAddress = "";
    private static String originationNumber = "";
    private static String destinationNumber = "";
    private static String message = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_EAST_1;
        pinpoint = PinpointClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        // Set the VoiceClient.
        // Set the content type to application/json.
        List<String> listVal = new ArrayList<>();
        listVal.add("application/json");

        Map<String, List<String>> values = new HashMap<>();
        values.put("Content-Type", listVal);

        ClientOverrideConfiguration config2 = ClientOverrideConfiguration.builder()
                .headers(values)
                .build();

        voiceClient = PinpointSmsVoiceClient.builder()
                .overrideConfiguration(config2)
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = AmazonPinpointTest.class.getClassLoader().getResourceAsStream("config.properties")) {

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
            userId = prop.getProperty("userId");
            s3BucketName = prop.getProperty("s3BucketName");
            s3BucketName = prop.getProperty("s3BucketName");
            iamExportRoleArn = prop.getProperty("iamExportRoleArn");
            existingApplicationId= prop.getProperty("existingApplicationId");
            filePath= prop.getProperty("filePath");
            subject = prop.getProperty("subject");
            senderAddress = prop.getProperty("senderAddress");
            toAddress = prop.getProperty("toAddress");
            originationNumber= prop.getProperty("originationNumber");
            destinationNumber= prop.getProperty("destinationNumber");
            message= prop.getProperty("message");


        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSPinpointService_thenNotNull() {
        assertNotNull(pinpoint);
        assertNotNull(voiceClient);
        assertNotNull(s3Client);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateApp() {

      appId = CreateApp.createApplication(pinpoint, appName);
      assertTrue(!appId.isEmpty());
      System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void UpdateEndpoint()
    {
       EndpointResponse response = UpdateEndpoint.createEndpoint(pinpoint, appId);
       endpointId2 = response.id() ;
       assertTrue(!endpointId2.isEmpty());
       System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void LookUpEndpoint()
    {
        LookUpEndpoint.lookupPinpointEndpoint(pinpoint, appId, endpointId2);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void AddExampleUser()
    {
        AddExampleUser.updatePinpointEndpoint(pinpoint,appId,endpointId2);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void AddExampleEndpoints()
    {
         AddExampleEndpoints.updateEndpointsViaBatch(pinpoint,appId);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void DeleteEndpoint() {

        DeleteEndpoint.deletePinEncpoint(pinpoint, appId, endpointId2 );
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void SendMessage() {

      SendMessage.sendSMSMessage(pinpoint, message, existingApplicationId, originationNumber, destinationNumber);
     //   SendMessage.sendSMSMessage(pinpoint, message, "2fdc4442c6a2483f85eaf7a943054815", originationNumber, destinationNumber);

       System.out.println("Test 8 passed");
    }

  //  @Test
  //  @Order(9)
  //  public void ImportSegments() {

    //   ImportSegment.createImportSegment(pinpoint, existingApplicationId, bucket, path, roleArn);
    //   System.out.println("Test 9 passed");
   // }

    @Test
    @Order(10)
    public void ListSegments() {

     ListSegments.listSegs(pinpoint, appId);
     System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void CreateSegment() {

        SegmentResponse createSegmentResult =  CreateSegment.createSegment(pinpoint, existingApplicationId);
        segmentId =  createSegmentResult.id();
        assertTrue(!segmentId.isEmpty());
        System.out.println("Test 11 passed");
   }

    @Test
    @Order(12)
    public void CreateCampaign() {

       CreateCampaign.createPinCampaign(pinpoint, existingApplicationId, segmentId );
       System.out.println("Test 12 passed");
    }


    @Test
    @Order(13)
    public void ExportEndpoints() {

        ExportEndpoints.exportAllEndpoints(pinpoint, s3Client, existingApplicationId, s3BucketName, filePath, iamExportRoleArn);
        System.out.println("Test 13 passed");
    }

    @Test
    @Order(14)
    public void SendEmailMessage() {
        SendEmailMessage.sendEmail(pinpoint, subject, existingApplicationId,  senderAddress, toAddress);
        System.out.println("Test 14 passed");
   }

    @Test
    @Order(15)
   public void SendVoiceMessage() {
       SendVoiceMessage.sendVoiceMsg(voiceClient, originationNumber, destinationNumber);
        System.out.println("Test 15 passed");
   }

    @Test
    @Order(16)
   public void ListEndpointIds() {
        ListEndpointIds.listAllEndpoints(pinpoint, existingApplicationId, userId);
        System.out.println("Test 16 passed");
   }

   @Test
    @Order(17)
    public void DeleteApp() {
         DeleteApp.deletePinApp(pinpoint, appId);
         System.out.println("Test 17 passed");
    }
}
