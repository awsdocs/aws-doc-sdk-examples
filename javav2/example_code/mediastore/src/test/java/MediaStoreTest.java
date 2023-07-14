/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.mediastore.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient ;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MediaStoreTest {
    private static MediaStoreClient mediaStoreClient;
    private static  MediaStoreDataClient mediaStoreData;
    private static String containerName ="";
    private static String filePath ="";
    private static String completePath ="";
    private static String existingContainer ="";
    private static String savePath ="";

    @BeforeAll
    public static void setUp() throws URISyntaxException {
        Region region = Region.US_EAST_1;
        mediaStoreClient = MediaStoreClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        containerName = values.getContainerName()+ java.util.UUID.randomUUID();
        filePath = values.getFilePath();
        completePath = values.getCompletePath();
        existingContainer = values.getExistingContainer();
        savePath = values.getSavePath();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = MediaStoreTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests
            prop.load(input);
            containerName = prop.getProperty("containerName")+ java.util.UUID.randomUUID();
            filePath = prop.getProperty("filePath");
            completePath = prop.getProperty("completePath");
            existingContainer = prop.getProperty("existingContainer");
            savePath = prop.getProperty("savePath");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */

        URI uri = new URI(PutObject.getEndpoint(existingContainer));
        mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateContainer() {
        assertDoesNotThrow(() ->CreateContainer.createMediaContainer(mediaStoreClient, containerName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeContainer() {
        assertDoesNotThrow(() ->DescribeContainer.checkContainer(mediaStoreClient, containerName));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListContainers() {
        assertDoesNotThrow(() ->ListContainers.listAllContainers(mediaStoreClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
   public void PutObject() throws URISyntaxException {
       Region region = Region.US_EAST_1;
       URI uri = new URI(getEndpoint(containerName));
       MediaStoreDataClient mediaStoreDataOb = MediaStoreDataClient.builder()
           .endpointOverride(uri)
           .region(region)
           .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
           .build();


       assertDoesNotThrow(() -> PutObject.putMediaObject(mediaStoreDataOb, filePath, completePath));
       System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListItems() {
        assertDoesNotThrow(() ->ListItems.listAllItems(mediaStoreData, containerName));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void GetObject() throws URISyntaxException {
        URI uri = new URI(getEndpoint(containerName));
        Region region = Region.US_EAST_1;
        MediaStoreDataClient mediaStoreDataOb = MediaStoreDataClient.builder()
            .endpointOverride(uri)
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        assertDoesNotThrow(() ->GetObject.getMediaObject(mediaStoreDataOb, completePath, savePath));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void DeleteObject() throws URISyntaxException {
        URI uri = new URI(getEndpoint(containerName));
        Region region = Region.US_EAST_1;
        MediaStoreDataClient mediaStoreDataOb = MediaStoreDataClient.builder()
            .endpointOverride(uri)
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        assertDoesNotThrow(() ->DeleteObject.deleteMediaObject(mediaStoreDataOb, completePath));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeleteContainer() throws InterruptedException {
        System.out.println("Wait 1 min to delete container");
        TimeUnit.MINUTES.sleep(1);
        assertDoesNotThrow(() ->assertDoesNotThrow(() ->DeleteContainer.deleteMediaContainer(mediaStoreClient, containerName)));
        System.out.println("Test 7 passed");
    }

    private static String getEndpoint(String containerName){
        Region region = Region.US_EAST_1;
        MediaStoreClient mediaStoreClient = MediaStoreClient.builder()
            .region(region)
            .build();

        DescribeContainerRequest containerRequest = DescribeContainerRequest.builder()
            .containerName(containerName)
            .build();

        DescribeContainerResponse response = mediaStoreClient.describeContainer(containerRequest);
        return response.container().endpoint();
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/mediastore";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/mediastore (an AWS Secrets Manager secret)")
    class SecretValues {
        private String containerName;
        private String existingContainer;
        private String filePath;

        private String savePath;

        private String completePath;

        public String getContainerName() {
            return containerName;
        }

        public String getExistingContainer() {
            return existingContainer;
        }

        public String getFilePath() {
            return filePath;
        }

        public String getSavePath() {
            return savePath;
        }

        public String getCompletePath() {
            return completePath;
        }
    }
}

