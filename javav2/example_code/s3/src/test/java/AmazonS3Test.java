/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.s3.*;
import com.example.s3.util.MemoryLog4jAppender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3control.S3ControlClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonS3Test {

    private static S3Client s3;
    private static S3Presigner presigner;
    private static S3ControlClient s3ControlClient;

    // Define the data members required for the tests.
    private static String bucketName = "<Enter a bucket name>";
    private static String objectKey = "";
    private static String objectPath = "";
    private static String toBucket = "";
    private static String policyText = "";
    private static String id = "";
    private static String presignKey="";
    private static String presignBucket="";
    private static String path="";
    private static String bucketNamePolicy="";
    private static String accountId="";
    private static String accessPointName="";
    private static String bucketNameZip="";

    // Used for the encryption test.
    private static String encryptObjectName="";
    private static String encryptObjectPath="";
    private static String encryptOutPath="";
    private static String keyId="";

    // Used for restore tests.
    private static String restoreImagePath = "";
    private static String restoreBucket = "";
    private static String restoreImageName = "";

    // Used for the Scenario test.
    private static String bucketNameSc = "";
    private static String keySc = "";
    private static String objectPathSc = "";
    private static String savePathSc = "";
    private static String toBucketSc = "";
    private static String images= "";
    private static String[] imageKeys ;

    @BeforeAll
    public static void setUp() throws IOException {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();

        presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();

        s3ControlClient = S3ControlClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(credentialsProvider)
                .build();

        try (InputStream input = AmazonS3Test.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            bucketName = prop.getProperty("bucketName");
            objectKey = prop.getProperty("objectKey");
            objectPath= prop.getProperty("objectPath");
            toBucket = prop.getProperty("toBucket");
            policyText = prop.getProperty("policyText");
            id  = prop.getProperty("id");
            presignKey = prop.getProperty("presignKey");
            presignBucket= prop.getProperty("presignBucket");
            path = prop.getProperty("path");
            bucketNamePolicy = prop.getProperty("bucketNamePolicy");
            accountId = prop.getProperty("accountId");
            accessPointName = prop.getProperty("accessPointName");
            encryptObjectName = prop.getProperty("encryptObjectName");
            encryptObjectPath = prop.getProperty("encryptObjectPath");
            encryptOutPath = prop.getProperty("encryptOutPath");
            keyId = prop.getProperty("keyId");
            restoreImagePath = prop.getProperty("restoreImagePath");
            restoreBucket = prop.getProperty("restoreBucket");
            restoreImageName = prop.getProperty("restoreImageName");
            bucketNameSc = prop.getProperty("bucketNameSc");
            keySc = prop.getProperty("keySc");
            objectPathSc = prop.getProperty("objectPathSc");
            savePathSc = prop.getProperty("savePathSc");
            toBucketSc = prop.getProperty("toBucketSc");
            bucketNameZip = prop.getProperty("bucketNameZip");
            images = prop.getProperty("images");
           imageKeys = images.split("[,]", 0);

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
       CreateBucket.createBucket(s3,bucketName);
       System.out.println("Test 2 passed");
   }

    @Test
    @Order(3)
   public void putObject() {
       PutObject.putS3Object(s3, bucketName, objectKey, objectPath);
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
        String polText = SetBucketPolicy.getBucketPolicyFromFile(policyText);
        assertTrue(!polText.isEmpty());
        SetBucketPolicy.setPolicy(s3, bucketNamePolicy, polText);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void getBucketPolicy() throws InterruptedException {
        // Sleep while the policy takes effect
        TimeUnit.SECONDS.sleep(5);
        String polText = GetBucketPolicy.getPolicy(s3, bucketNamePolicy);
        assertTrue(!polText.isEmpty());
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void deleteBucketPolicy() {
        DeleteBucketPolicy.deleteS3BucketPolicy(s3,bucketNamePolicy );
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void setBucketACL() {
        System.out.format("Running Amazon S3 Test 8");
        System.out.println("for object: " + objectKey);
        System.out.println(" in bucket: " + bucketName);
        SetAcl.setBucketAcl(s3, bucketName, id);
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
    public void generatePresignedUrlAndUploadObject() {
        GeneratePresignedUrlAndUploadObject.signBucket(presigner, presignBucket, presignKey);
        System.out.println("Test 10 passed");
    }

    @Test
    @Order(11)
    public void getObjectPresignedUrl() {
        GetObjectPresignedUrl.getPresignedUrl(presigner, presignBucket, presignKey);
        System.out.println("Test 11 passed");
    }

    @Test
    @Order(12)
    public void getObjectData() {
        GetObjectData.getObjectBytes(s3,bucketName,objectKey, path);
        System.out.println("Test 12 passed");
    }

    @Test
    @Order(13)
    public void listObjects() {
        ListObjects.listBucketObjects(s3,bucketName);
        System.out.println("Test 13 passed");
    }

    @Test
    @Order(14)
    public void createAccessPoint() {
        CreateAccessPoint.createSpecificAccessPoint(s3ControlClient, accountId, bucketName, accessPointName);
        CreateAccessPoint.deleteSpecificAccessPoint(s3ControlClient, accountId, accessPointName);
        System.out.println("Test 14 passed");
    }

    @Test
    @Order(15)
    public void  lifecycleConfiguration() {
        LifecycleConfiguration.setLifecycleConfig(s3, bucketName, accountId);
        LifecycleConfiguration.getLifecycleConfig(s3, bucketName, accountId);
        LifecycleConfiguration.deleteLifecycleConfig(s3, bucketName, accountId);
        System.out.println("Test 16 passed");
    }

    @Test
    @Order(16)
    public void S3Cors() {
        S3Cors.setCorsInformation(s3, bucketName, accountId);
        S3Cors.getBucketCorsInformation(s3, bucketName, accountId);
        S3Cors.deleteBucketCorsInformation(s3, bucketName, accountId);
        System.out.println("Test 17 passed");
    }

    @Test
    @Order(17)
    public void deleteMultiObjects() {
        DeleteMultiObjects.deleteBucketObjects(s3, bucketName);
        System.out.println("Test 18 passed");
    }

    @Test
    @Order(18)
    public void deleteObjects() {
        DeleteObjects.deleteBucketObjects(s3,bucketName,objectKey);
        DeleteObjects.deleteBucketObjects(s3,bucketName,encryptObjectName);
        System.out.println("Test 19 passed");
    }

    @Test
    @Order(19)
    public void copyObjectStorage() {
        PutObject.putS3Object(s3, restoreBucket, restoreImageName, restoreImagePath);
        CopyObjectStorage.copyBucketObject(s3,restoreBucket, restoreImageName, restoreBucket);
        System.out.println("Test 19 passed");
    }

    @Test
    @Order(20)
    public void restoreObject() {
        RestoreObject.restoreS3Object(s3, restoreBucket, restoreImageName, accountId);
        System.out.println("Test 20 passed");
    }

    @Test
    @Order(21)
    public void getRestoreStatus() {
        GetObjectRestoreStatus.checkStatus(s3, restoreBucket, restoreImageName);
        System.out.println("Test 21 passed");
    }

    @Test
    @Order(22)
    public void S3ZipExample() {
        S3ZipExample.createZIPFile(s3, bucketNameZip, imageKeys);
        System.out.println("Test 21 passed");
    }

    @Test
    @Order(23)
    public void testScenario() {
        System.out.println(S3Scenario.DASHES);
        System.out.println("1. Create an Amazon S3 bucket.");
        S3Scenario.createBucket(s3, bucketNameSc);
        System.out.println( S3Scenario.DASHES);

        System.out.println( S3Scenario.DASHES);
        System.out.println("2. Update a local file to the Amazon S3 bucket.");
        S3Scenario.uploadLocalFile(s3, bucketNameSc, keySc, objectPathSc);
        System.out.println(S3Scenario.DASHES);

        System.out.println( S3Scenario.DASHES);
        System.out.println("3. Download the object to another local file.");
        S3Scenario.getObjectBytes (s3, bucketNameSc, keySc, savePathSc);
        System.out.println( S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("4. Perform a multipart upload.");
        String multipartKey = "multiPartKey";
        S3Scenario.multipartUpload(s3, toBucketSc, multipartKey);
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("5. List all objects located in the Amazon S3 bucket.");
        S3Scenario.listAllObjects(s3, bucketNameSc);
        S3Scenario.anotherListExample(s3, bucketNameSc) ;
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("6. Copy the object to another Amazon S3 bucket.");
        S3Scenario.copyBucketObject (s3, bucketNameSc, keySc, toBucketSc);
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("7. Delete the object from the Amazon S3 bucket.");
        S3Scenario.deleteObjectFromBucket(s3, bucketNameSc, keySc);
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("8. Delete the Amazon S3 bucket.");
        S3Scenario.deleteBucket(s3, bucketNameSc);
        System.out.println(S3Scenario.DASHES);
    }
    @Test
    @Order(24)
    public void s3UriParsingTest(){
        String url = "https://s3.us-west-1.amazonaws.com/myBucket/resources/doc.txt?versionId=abc123&partNumber=77&partNumber=88";
        ParseUri.parseS3UriExample(s3,url);
        final LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        final Configuration configuration = context.getConfiguration();
        final MemoryLog4jAppender memoryLog4jAppender = (MemoryLog4jAppender) configuration.getAppender("MemoryLog4jAppender");
        final Map<String, String> eventMap = memoryLog4jAppender.getEventMap();

        Assertions.assertTrue(() -> eventMap.get("region").equals("us-west-1"));
        Assertions.assertTrue(() -> eventMap.get("bucket").equals("myBucket"));
        Assertions.assertTrue(() -> eventMap.get("key").equals("resources/doc.txt"));
        Assertions.assertTrue(() -> eventMap.get("isPathStyle").equals("true"));
        Assertions.assertTrue(() -> eventMap.get("rawQueryParameters").equals("{versionId=[abc123], partNumber=[77, 88]}"));
        Assertions.assertTrue(() -> eventMap.get("firstMatchingRawQueryParameter-versionId").equals("abc123"));
        Assertions.assertTrue(() -> eventMap.get("firstMatchingRawQueryParameter-partNumber").equals("77"));
        Assertions.assertTrue(() -> eventMap.get("firstMatchingRawQueryParameter").equals("[77, 88]"));
    }
}
