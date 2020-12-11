/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
import com.example.rekognition.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import java.io.*;
import java.util.Properties;
import  software.amazon.awssdk.regions.Region;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RekognitionTest {

    private static RekognitionClient rekClient;
    private static String facesImage="";
    private static String celebritiesImage ="";
    private static String faceImage2 ="";
    private static String celId="";
    private static String moutainImage="";
    private static String collectionName="";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_WEST_2;
        rekClient = RekognitionClient.builder()
                .region(region)
                .build();

        try (InputStream input = RekognitionTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file
            prop.load(input);
            facesImage = prop.getProperty("facesImage");
            celebritiesImage = prop.getProperty("celebritiesImage");
            faceImage2 = prop.getProperty("faceImage2");
            celId = prop.getProperty("celId");
            moutainImage = prop.getProperty("moutainImage");
            collectionName = prop.getProperty("collectionName");

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
   public void SearchFaceMatchingImageCollection() {
       SearchFaceMatchingImageCollection.searchFaceInCollection(rekClient, collectionName, faceImage2);
        System.out.println("Test 11 passed");
   }

    @Test
    @Order(12)
    public void DeleteCollection() {
        DeleteCollection.deleteMyCollection(rekClient, collectionName);
        System.out.println("Test 12 passed");
    }
}
