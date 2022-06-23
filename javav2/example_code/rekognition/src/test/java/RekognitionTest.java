/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.rekognition.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import java.io.*;
import java.util.Properties;
import  software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.s3.S3Client;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RekognitionTest {

    private static RekognitionClient rekClient;
    private static  S3Client s3;
    private static NotificationChannel channel;
    private static String facesImage="";
    private static String celebritiesImage ="";
    private static String faceImage2 ="";
    private static String celId="";
    private static String moutainImage="";
    private static String collectionName="";
    private static String ppeImage="";
    private static String bucketName="";
    private static String textImage="";
    private static String modImage="" ;
    private static String faceVid="" ;
    private static String topicArn ="" ;
    private static String roleArn ="" ;
    private static String modVid = "";
    private static String textVid="";
    private static String celVid="";


    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_EAST_1;
        rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = RekognitionTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            facesImage = prop.getProperty("facesImage");
            celebritiesImage = prop.getProperty("celebritiesImage");
            faceImage2 = prop.getProperty("faceImage2");
            celId = prop.getProperty("celId");
            moutainImage = prop.getProperty("moutainImage");
            collectionName = prop.getProperty("collectionName");
            ppeImage = prop.getProperty("ppeImage");
            bucketName = prop.getProperty("bucketName");
            textImage= prop.getProperty("textImage");
            modImage= prop.getProperty("modImage");
            faceVid = prop.getProperty("faceVid");
            topicArn= prop.getProperty("topicArn");
            roleArn = prop.getProperty("roleArn");
            modVid= prop.getProperty("modVid");
            textVid = prop.getProperty("textVid");
            celVid= prop.getProperty("celVid");


            // Required for tests that involve videos
            channel = NotificationChannel.builder()
                    .snsTopicArn(topicArn)
                    .roleArn(roleArn)
                    .build();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
  }

    @Test
    @Order(1)
    public void whenInitializingAWSRekognitionService_thenNotNull() {
        assertNotNull(rekClient);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void DetectFaces() {
        DetectFaces.detectFacesinImage(rekClient, facesImage);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void RecognizeCelebrities() {
        RecognizeCelebrities.recognizeAllCelebrities(rekClient, celebritiesImage);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void CompareFaces() {
        CompareFaces.compareTwoFaces(rekClient, 70F, facesImage, faceImage2);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void CelebrityInfo() {
        CelebrityInfo.getCelebrityInfo(rekClient, celId);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void DetectLabels() {

        DetectLabels.detectImageLabels(rekClient, moutainImage);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void CreateCollection() {
        CreateCollection.createMyCollection(rekClient, collectionName);
        System.out.println("Test 7 passed");
        }

    @Test
    @Order(8)
    public void AddFacesToCollection() {
        AddFacesToCollection.addToCollection(rekClient, collectionName, facesImage );
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void ListFacesCollection() {
        ListFacesInCollection.listFacesCollection(rekClient, collectionName);
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
   public void ListCollections() {

       ListCollections.listAllCollections(rekClient);
        System.out.println("Test 10 passed");
   }

    @Test
    @Order(11)
   public void DescribeCollection() {
       DescribeCollection.describeColl(rekClient, collectionName);
       System.out.println("Test 11 passed");
  }

    @Test
    @Order(12)
   public void SearchFaceMatchingImageCollection() {
       SearchFaceMatchingImageCollection.searchFaceInCollection(rekClient, collectionName, faceImage2);
        System.out.println("Test 12 passed");
   }

    @Test
    @Order(13)
   public void DetectPPE() {
       DetectPPE.displayGear(s3, rekClient, ppeImage, bucketName);
        System.out.println("Test 13 passed");
   }

    @Test
    @Order(14)
   public void DetectText() {
       DetectText.detectTextLabels(rekClient, textImage);
        System.out.println("Test 14 passed");
   }

    @Test
    @Order(15)
   public void DetectModerationLabels() {
       DetectModerationLabels.detectModLabels(rekClient, modImage);
       System.out.println("Test 15 passed");
   }

    @Test
    @Order(16)
   public void VideoDetectFaces() {

       VideoDetectFaces.StartFaceDetection(rekClient, channel, bucketName, celVid);
       VideoDetectFaces.GetFaceResults(rekClient);
       System.out.println("Test 16 passed");
   }

    @Test
    @Order(17)
   public void VideoDetectInappropriate() {
       VideoDetectInappropriate.startModerationDetection(rekClient, channel, bucketName, modVid);
       VideoDetectInappropriate.GetModResults(rekClient);
        System.out.println("Test 17 passed");
   }

    @Test
    @Order(18)
   public void VideoDetectText() {
       VideoDetectText.startTextLabels(rekClient, channel, bucketName, textVid);
       VideoDetectText.GetTextResults(rekClient);
        System.out.println("Test 18 passed");
   }

    @Test
    @Order(19)
   public void VideoPersonDetection() {

       VideoPersonDetection.startPersonLabels(rekClient, channel, bucketName, faceVid);
       VideoPersonDetection.GetPersonDetectionResults(rekClient);
       System.out.println("Test 19 passed");
   }

    @Test
    @Order(20)
    public void DeleteCollection() {
        DeleteCollection.deleteMyCollection(rekClient, collectionName);
        System.out.println("Test 20 passed");
    }
}
