// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.s3.CreateAccessPoint;
import com.example.s3.DeleteBucketPolicy;
import com.example.s3.DeleteMultiObjects;
import com.example.s3.GetObjectPresignedUrl;
import com.example.s3.GetObjectRestoreStatus;
import com.example.s3.LifecycleConfiguration;
import com.example.s3.S3Cors;
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
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3control.S3ControlClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonS3Test {
    private static S3Client s3;
    private static S3Presigner presigner;
    private static S3ControlClient s3ControlClient;

    // Define the data members required for the tests.
    private static String bucketName = "";

    private static String presignKey = "";
    private static String presignBucket = "";
    private static String bucketNamePolicy = "";
    private static String accountId = "";
    private static String accessPointName = "";




    // Used for restore tests.
    private static String restoreImagePath = "";
    private static String restoreBucket = "";
    private static String restoreImageName = "";



    private static S3Actions s3Actions;

    @BeforeAll
    public static void setUp() throws IOException {

        s3Actions = new S3Actions();
        s3 = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        s3ControlClient = S3ControlClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        bucketName = values.getBucketName() + java.util.UUID.randomUUID();
        presignKey = values.getPresignKey();
        presignBucket = values.getPresignBucket();
        bucketNamePolicy = values.getBucketNamePolicy();
        accountId = values.getAccountId();
        accessPointName = values.getAccessPointName();
        restoreImagePath = values.getRestoreImagePath();
        restoreBucket = values.getRestoreBucket();
        restoreImageName = values.getRestoreImageName();

        // Create the S3 bucket to be used for testing
        CompletableFuture<Void> createBucketFuture = s3Actions.createBucketAsync(bucketName);
        createBucketFuture.join(); // Wait for bucket creation


    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void deleteBucketPolicy() {
        assertDoesNotThrow(() -> DeleteBucketPolicy.deleteS3BucketPolicy(s3, bucketNamePolicy));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void getObjectPresignedUrl() {
        assertDoesNotThrow(() -> GetObjectPresignedUrl.getPresignedUrl(presigner, presignBucket, presignKey));
        System.out.println("Test 2 passed");
    }



    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void createAccessPoint() {
        assertDoesNotThrow(() -> CreateAccessPoint.createSpecificAccessPoint(s3ControlClient, accountId, bucketName,
                accessPointName));
        assertDoesNotThrow(
                () -> CreateAccessPoint.deleteSpecificAccessPoint(s3ControlClient, accountId, accessPointName));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void lifecycleConfiguration() {
        assertDoesNotThrow(() -> LifecycleConfiguration.setLifecycleConfig(s3, bucketName, accountId));
        assertDoesNotThrow(() -> LifecycleConfiguration.getLifecycleConfig(s3, bucketName, accountId));
        assertDoesNotThrow(() -> LifecycleConfiguration.deleteLifecycleConfig(s3, bucketName, accountId));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void s3Cors() {
        assertDoesNotThrow(() -> S3Cors.setCorsInformation(s3, bucketName, accountId));
        assertDoesNotThrow(() -> S3Cors.getBucketCorsInformation(s3, bucketName, accountId));
        assertDoesNotThrow(() -> S3Cors.deleteBucketCorsInformation(s3, bucketName, accountId));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void deleteMultiObjects() {
        assertDoesNotThrow(() -> DeleteMultiObjects.deleteBucketObjects(s3, bucketName));
        System.out.println("Test 6 passed");
    }



    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void getRestoreStatus() {
        assertDoesNotThrow(() -> GetObjectRestoreStatus.checkStatus(s3, restoreBucket, restoreImageName));
        System.out.println("Test 7 passed");
    }

    @AfterAll
    static void teardown() throws Exception {
        s3Actions.getAsyncClient().deleteBucket(DeleteBucketRequest.builder().bucket(bucketName).build()).join();

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
        private String bucketName;
        private String bucketNamePolicy;
        private String presignBucket;

        private String objectKey;

        private String presignKey;
        private String path;

        private String objectPath;

        private String toBucket;
        private String policyText;

        private String id;

        private String accountId;

        private String accessPointName;

        private String encryptObjectName;

        private String encryptObjectPath;

        private String encryptOutPath;

        private String keyId;

        private String restoreImagePath;

        private String restoreBucket;

        private String restoreImageName;

        private String bucketNameSc;

        private String keySc;

        private String objectPathSc;

        private String savePathSc;

        private String toBucketSc;

        private String bucketNameZip;

        private String images;

        public String getBucketName() {
            return bucketName;
        }

        public String getBucketNamePolicy() {
            return bucketNamePolicy;
        }

        public String getPresignBucket() {
            return presignBucket;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getPresignKey() {
            return presignKey;
        }

        public String getPath() {
            return path;
        }

        public String getObjectPath() {
            return objectPath;
        }

        public String getToBucket() {
            return toBucket;
        }

        public String getPolicyText() {
            return policyText;
        }

        public String getId() {
            return id;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getAccessPointName() {
            return accessPointName;
        }

        public String getEncryptObjectName() {
            return encryptObjectName;
        }

        public String getEncryptObjectPath() {
            return encryptObjectPath;
        }

        public String getEncryptOutPath() {
            return encryptOutPath;
        }

        public String getKeyId() {
            return keyId;
        }

        public String getRestoreImagePath() {
            return restoreImagePath;
        }

        public String getRestoreBucket() {
            return restoreBucket;
        }

        public String getRestoreImageName() {
            return restoreImageName;
        }

        public String getBucketNameSc() {
            return bucketNameSc;
        }

        public String getKeySc() {
            return keySc;
        }

        public String getObjectPathSc() {
            return objectPathSc;
        }

        public String getSavePathSc() {
            return savePathSc;
        }

        public String getToBucketSc() {
            return toBucketSc;
        }

        public String getBucketNameZip() {
            return bucketNameZip;
        }

        public String getImages() {
            return images;
        }
    }
}
