// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.s3.scenario.S3Actions;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3ActionsTest {
    private static final Logger logger = LoggerFactory.getLogger(S3ActionsTest.class);
    private static S3Actions s3Actions;
    private static final String BUCKET_NAME = "test-bucket-" + System.currentTimeMillis();
    private static String path = "";
    private static String objectPath = "";

    private static String objectKey = "";

    @BeforeAll
    static void setup() throws Exception {
        s3Actions = new S3Actions();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        objectPath = values.getObjectPath();
        objectKey = values.getObjectKey();
        path = values.getPath();

        // Create a test file to upload
        Path path1 = Paths.get(path);
        if (!Files.exists(path1)) {
            Files.write(path1, "This is a test file.".getBytes());
        }

        CompletableFuture<Void> createBucketFuture = s3Actions.createBucketAsync(BUCKET_NAME);
        createBucketFuture.join(); // Wait for bucket creation
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    void testUploadLocalFileAsync() {
        // Ensure the method doesn't throw any exceptions
        assertDoesNotThrow(() -> {
            CompletableFuture<PutObjectResponse> future = s3Actions.uploadLocalFileAsync(BUCKET_NAME, objectKey, path);
            future.join(); // Wait for the operation to complete
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    void testGetObjectBytesAsync() {
        // Ensure the method doesn't throw any exceptions
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = s3Actions.getObjectBytesAsync(BUCKET_NAME, objectKey, "downloaded-file.txt");
            future.join(); // Wait for the operation to complete
        });
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    void testGetObjectBytesAsyncWithException() {
        String invalidKey = "non-existent-key";
        CompletableFuture<Void> future = s3Actions.getObjectBytesAsync(BUCKET_NAME, invalidKey, "downloaded-file.txt");
        assertThrows(RuntimeException.class, future::join);
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    void testListAllObjectsAsync() {
        assertDoesNotThrow(() -> {
            CompletableFuture<Void> future = s3Actions.listAllObjectsAsync(BUCKET_NAME);
            future.join(); // Wait for the operation to complete
        });
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    void testListAllObjectsAsyncWithException() {
         String invalidBucket = "non-existent-bucket";
        CompletableFuture<Void> future = s3Actions.listAllObjectsAsync(invalidBucket);
        assertThrows(RuntimeException.class, future::join);
        logger.info("Test 5 passed");
    }

    @AfterAll
    static void teardown() throws Exception {
        // Delete the S3 bucket and the test file after testing
        s3Actions.deleteObjectFromBucketAsync(BUCKET_NAME, objectKey).join();
        s3Actions.getAsyncClient().deleteBucket(DeleteBucketRequest.builder().bucket(BUCKET_NAME).build()).join();
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/s3";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/s3 (an AWS Secrets Manager secret)")
    class SecretValues {
        private String objectKey;

        private String path;

        private String objectPath;

        public String getObjectKey() {
            return objectKey;
        }


        public String getPath() {
            return path;
        }

        public String getObjectPath() {
            return objectPath;
        }

    }
}
