// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.mediastore.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.mediastore.MediaStoreClient;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerRequest;
import software.amazon.awssdk.services.mediastore.model.DescribeContainerResponse;
import software.amazon.awssdk.services.mediastoredata.MediaStoreDataClient;
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
    private static final Logger logger = LoggerFactory.getLogger(MediaStoreTest.class);
    private static MediaStoreClient mediaStoreClient;
    private static MediaStoreDataClient mediaStoreData;
    private static String containerName = "";
    private static String filePath = "";
    private static String completePath = "";
    private static String existingContainer = "";
    private static String savePath = "";

    @BeforeAll
    public static void setUp() throws URISyntaxException {
        Region region = Region.US_EAST_1;
        mediaStoreClient = MediaStoreClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        containerName = values.getContainerName() + java.util.UUID.randomUUID();
        filePath = values.getFilePath();
        completePath = values.getCompletePath();
        existingContainer = values.getExistingContainer();
        savePath = values.getSavePath();

        URI uri = new URI(PutObject.getEndpoint(existingContainer));
        mediaStoreData = MediaStoreDataClient.builder()
                .endpointOverride(uri)
                .region(region)
                .build();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateContainer() {
        assertDoesNotThrow(() -> CreateContainer.createMediaContainer(mediaStoreClient, containerName));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeContainer() {
        assertDoesNotThrow(() -> DescribeContainer.checkContainer(mediaStoreClient, containerName));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testListContainers() {
        assertDoesNotThrow(() -> ListContainers.listAllContainers(mediaStoreClient));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListItems() {
        assertDoesNotThrow(() -> ListItems.listAllItems(mediaStoreData, containerName));
        logger.info("Test 5 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDeleteContainer() throws InterruptedException {
        System.out.println("Wait 1 min to delete container");
        TimeUnit.MINUTES.sleep(1);
        assertDoesNotThrow(
                () -> assertDoesNotThrow(() -> DeleteContainer.deleteMediaContainer(mediaStoreClient, containerName)));
        logger.info("Test 5 passed");
    }

    private static String getEndpoint(String containerName) {
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
