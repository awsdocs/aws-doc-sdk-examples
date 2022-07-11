/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.mediastore.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient ;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MediaStoreTest {

    private static MediaStoreClient mediaStoreClient;
    private static  MediaStoreDataClient mediaStoreData;
    private static String containerName ="";
    private static String putPath ="";
    private static String filePath ="";
    private static String existingContainer ="";
    private static String savePath="";
    private static String getPath="";

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        Region region = Region.US_EAST_1;
        mediaStoreClient = MediaStoreClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        try (InputStream input = MediaStoreTest.class.getClassLoader().getResourceAsStream("config.properties")) {

            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            //load a properties file from class path, inside static method
            prop.load(input);

            // Populate the data members required for all tests
            containerName = prop.getProperty("containerName");
            putPath = prop.getProperty("putPath");
            filePath = prop.getProperty("filePath");
            existingContainer = prop.getProperty("existingContainer");
            savePath = prop.getProperty("savePath");
            getPath = prop.getProperty("getPath");




        } catch (IOException ex) {
            ex.printStackTrace();
        }

        URI uri = new URI(PutObject.getEndpoint(existingContainer));
        mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();
    }

    @Test
    @Order(1)
    public void whenInitializingAWSService_thenNotNull() {
        assertNotNull(mediaStoreClient);
        assertNotNull(mediaStoreData);
        System.out.println("Test 1 passed");
    }

    @Test
    @Order(2)
    public void CreateContainer() {
       CreateContainer.createMediaContainer(mediaStoreClient, containerName);
        System.out.println("Test 2 passed");
    }

    @Test
    @Order(3)
    public void DescribeContainer() {
        DescribeContainer.checkContainer(mediaStoreClient, existingContainer);
        System.out.println("Test 3 passed");
    }

    @Test
    @Order(4)
    public void ListContainers() {
        ListContainers.listAllContainers(mediaStoreClient);
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void PutObject() {
        PutObject.putMediaObject(mediaStoreData, filePath, putPath);
        System.out.println("Test 5 passed");
    }

    @Test
    @Order(6)
    public void ListItems() {
        ListItems.listAllItems(mediaStoreData, existingContainer);
        System.out.println("Test 6 passed");
    }

    @Test
    @Order(7)
    public void GetObject() {
        GetObject.getMediaObject(mediaStoreData, getPath, savePath);
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void DeleteObject() {
        DeleteObject.deleteMediaObject(mediaStoreData, getPath);
        System.out.println("Test 8 passed");
    }

    @Test
    @Order(9)
    public void DeleteContainer() {
        DeleteContainer.deleteMediaContainer(mediaStoreClient, containerName);
        System.out.println("Test 9 passed");
    }

}
