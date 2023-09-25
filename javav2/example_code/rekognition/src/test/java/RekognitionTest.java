/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.rekognition.AddFacesToCollection;
import com.example.rekognition.CelebrityInfo;
import com.example.rekognition.CompareFaces;
import com.example.rekognition.CreateCollection;
import com.example.rekognition.DeleteCollection;
import com.example.rekognition.DescribeCollection;
import com.example.rekognition.DetectFaces;
import com.example.rekognition.DetectLabels;
import com.example.rekognition.DetectModerationLabels;
import com.example.rekognition.DetectPPE;
import com.example.rekognition.DetectText;
import com.example.rekognition.ListCollections;
import com.example.rekognition.ListFacesInCollection;
import com.example.rekognition.RecognizeCelebrities;
import com.example.rekognition.SearchFaceMatchingImageCollection;
import com.example.rekognition.VideoDetectFaces;
import com.example.rekognition.VideoDetectInappropriate;
import com.example.rekognition.VideoDetectText;
import com.example.rekognition.VideoPersonDetection;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import  software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.model.NotificationChannel;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
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
    public static void setUp() {

        Region region = Region.US_EAST_1;
        rekClient = RekognitionClient.builder()
                .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        s3 = S3Client.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        facesImage = values.getFacesImage();
        celebritiesImage = values.getCelebritiesImage();
        faceImage2 = values.getFaceImage2();
        celId = values.getCelId();
        moutainImage = values.getMoutainImage();
        collectionName = values.collectionName+ java.util.UUID.randomUUID();
        ppeImage = values.getPpeImage();
        bucketName = values.getBucketName();
        textImage= values.getTextImage();
        modImage= values.getModImage();
        faceVid = values.getFaceVid();
        topicArn= values.getTopicArn();
        roleArn =  values.getRoleArn();
        modVid= values.getModVid();
        textVid = values.getTextVid();
        celVid= values.getCelVid();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

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
            collectionName = prop.getProperty("collectionName")+ java.util.UUID.randomUUID();
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

        */
  }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void DetectFaces() {
        assertDoesNotThrow(() ->DetectFaces.detectFacesinImage(rekClient, facesImage));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void RecognizeCelebrities() {
        assertDoesNotThrow(() ->RecognizeCelebrities.recognizeAllCelebrities(rekClient, celebritiesImage));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void CompareFaces() {
        assertDoesNotThrow(() ->CompareFaces.compareTwoFaces(rekClient, 70F, facesImage, faceImage2));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void CelebrityInfo() {
        assertDoesNotThrow(() ->CelebrityInfo.getCelebrityInfo(rekClient, celId));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DetectLabels() {
        assertDoesNotThrow(() ->DetectLabels.detectImageLabels(rekClient, moutainImage));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void CreateCollection() {
        assertDoesNotThrow(() ->CreateCollection.createMyCollection(rekClient, collectionName));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void AddFacesToCollection() {
        assertDoesNotThrow(() ->AddFacesToCollection.addToCollection(rekClient, collectionName, facesImage));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void ListFacesCollection() {
        assertDoesNotThrow(() ->ListFacesInCollection.listFacesCollection(rekClient, collectionName));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
   public void ListCollections() {
       assertDoesNotThrow(() ->ListCollections.listAllCollections(rekClient));
       System.out.println("Test 9 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
   public void DescribeCollection() {
       assertDoesNotThrow(() ->DescribeCollection.describeColl(rekClient, collectionName));
       System.out.println("Test 10 passed");
  }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
   public void SearchFaceMatchingImageCollection() {
       assertDoesNotThrow(() ->SearchFaceMatchingImageCollection.searchFaceInCollection(rekClient, collectionName, faceImage2));
       System.out.println("Test 11 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
   public void DetectPPE() {
       assertDoesNotThrow(() ->DetectPPE.displayGear(s3, rekClient, ppeImage, bucketName));
       System.out.println("Test 12 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
   public void DetectText() {
       assertDoesNotThrow(() ->DetectText.detectTextLabels(rekClient, textImage));
       System.out.println("Test 13 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
   public void DetectModerationLabels() {
       assertDoesNotThrow(() ->DetectModerationLabels.detectModLabels(rekClient, modImage));
       System.out.println("Test 14 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
   public void VideoDetectFaces() {
       assertDoesNotThrow(() ->VideoDetectFaces.startFaceDetection(rekClient, channel, bucketName, celVid));
       assertDoesNotThrow(() ->VideoDetectFaces.getFaceResults(rekClient));
       System.out.println("Test 15 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
   public void VideoDetectInappropriate() {
       assertDoesNotThrow(() ->VideoDetectInappropriate.startModerationDetection(rekClient, channel, bucketName, modVid));
       assertDoesNotThrow(() ->VideoDetectInappropriate.getModResults(rekClient));
       System.out.println("Test 16 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(17)
   public void VideoDetectText() {
        assertDoesNotThrow(() ->VideoDetectText.startTextLabels(rekClient, channel, bucketName, textVid));
        assertDoesNotThrow(() ->VideoDetectText.getTextResults(rekClient));
        System.out.println("Test 17 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(18)
   public void VideoPersonDetection() {
       assertDoesNotThrow(() ->VideoPersonDetection.startPersonLabels(rekClient, channel, bucketName, faceVid));
       assertDoesNotThrow(() ->VideoPersonDetection.getPersonDetectionResults(rekClient));
       System.out.println("Test 18 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(19)
    public void DeleteCollection() {
        assertDoesNotThrow(() ->DeleteCollection.deleteMyCollection(rekClient, collectionName));
        System.out.println("Test 19 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/rekognition";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/rekognition (an AWS Secrets Manager secret)")
    class SecretValues {
        private String facesImage;
        private String faceImage2;
        private String celebritiesImage;

        private String celId;

        private String moutainImage;
        private String collectionName;

        private String ppeImage;
        private String textImage;

        private String modImage;

        private String bucketName;

        private String faceVid;

        private String modVid;

        private String textVid;

        private String celVid;

        private String topicArn;

        private String roleArn;

        public String getFacesImage() {
            return facesImage;
        }

        public String getFaceImage2() {
            return faceImage2;
        }

        public String getCelebritiesImage() {
            return celebritiesImage;
        }

        public String getCelId() {
            return celId;
        }

        public String getMoutainImage() {
            return moutainImage;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public String getPpeImage() {
            return ppeImage;
        }

        public String getTextImage() {
            return textImage;
        }

        public String getModImage() {
            return modImage;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getFaceVid() {
            return faceVid;
        }

        public String getModVid() {
            return modVid;
        }

        public String getTextVid() {
            return textVid;
        }

        public String getCelVid() {
            return celVid;
        }

        public String getTopicArn() {
            return topicArn;
        }

        public String getRoleArn() {
            return roleArn;
        }
    }
}

