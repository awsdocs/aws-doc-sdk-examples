import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.*;
import java.util.*;
import com.example.s3.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonS3ServiceIntegrationTest {

    private static S3Client s3;
    private static Region region;

    // Define the data members required for the tests
    private static String bucketName = "";
    private static String objectKey = "";
    private static String objectPath = "";
    private static String toBucket = "";
    private static String policyText = "";
    private static String id = "";
    private static String access = "";

    @BeforeAll
    public static void setUp() throws IOException {

        // Run tests on Real AWS Resources
        region = Region.US_WEST_2;
        s3 = S3Client.builder().region(region).build();
        try (InputStream input = AmazonS3ServiceIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            bucketName = prop.getProperty("bucketName");
            objectKey = prop.getProperty("objectKey");
            objectPath= prop.getProperty("objectPath");
            toBucket = prop.getProperty("toBucket");
            policyText = prop.getProperty("policyText");
            id  = prop.getProperty("id");
            access  = prop.getProperty("access");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    public void whenInitializingAWSS3Service_thenNotNull() {
        assertNotNull(s3);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
   public void createBucket() {

      S3ObjectOperations.createBucket(s3,bucketName,region);
      System.out.println("Test 2 passed");
   }

    @Test
    @Order(3)
   public void putObject() {

       String result = PutObject.putS3Object(s3, bucketName, objectKey, objectPath);
       assertTrue(!result.isEmpty());
       System.out.println("Test 3 passed");
   }

    @Test
    @Order(4)
   public void copyBucketObject() {

      String result = CopyObject.copyBucketObject(s3,bucketName,objectKey,toBucket);
      assertTrue(!result.isEmpty());
      System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void setBucketPolicy() {

     SetBucketPolicy.setPolicy(s3, bucketName, policyText);
     System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void getBucketPolicy() {

    String polText = GetBucketPolicy.getPolicy(s3, bucketName );
    assertTrue(!polText.isEmpty());
    System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void deleteBucketPolicy() {

   DeleteBucketPolicy.deleteS3BucketPolicy(s3,bucketName );
   System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void setBucketACL()
    {
        System.out.format("Running Amazon S3 Test 8");
        System.out.println("for object: " + objectKey);
        System.out.println(" in bucket: " + bucketName);
        SetAcl.setBucketAcl(s3, bucketName, id,access );
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void getACL(){

        String result = GetAcl.getBucketACL(s3,objectKey,bucketName);
        assertTrue(!result.isEmpty());
        System.out.println("Test 9 passed");
    }

     @Test
    @Order(10)
    public void deleteObjects() {

       DeleteObjects.deleteBucketObjects(s3,bucketName,objectKey);
       System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void deleteBucket() {

    S3ObjectOperations.deleteBucket(s3,bucketName);
    System.out.println("Test 11 passed");
    }
}
