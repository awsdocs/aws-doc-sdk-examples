// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.s3.directorybucket.AbortDirectoryBucketMultipartUploads;
import com.example.s3.directorybucket.CompleteDirectoryBucketMultipartUpload;
import com.example.s3.directorybucket.CopyDirectoryBucketObject;
import com.example.s3.directorybucket.CreateDirectoryBucket;
import com.example.s3.directorybucket.CreateDirectoryBucketMultipartUpload;
import com.example.s3.directorybucket.DeleteDirectoryBucket;
import com.example.s3.directorybucket.DeleteDirectoryBucketEncryption;
import com.example.s3.directorybucket.DeleteDirectoryBucketObject;
import com.example.s3.directorybucket.DeleteDirectoryBucketObjects;
import com.example.s3.directorybucket.DeleteDirectoryBucketPolicy;
import com.example.s3.directorybucket.GeneratePresignedGetURLForDirectoryBucket;
import com.example.s3.directorybucket.GetDirectoryBucketObject;
import com.example.s3.directorybucket.GetDirectoryBucketObjectAttributes;
import com.example.s3.directorybucket.GetDirectoryBucketPolicy;
import com.example.s3.directorybucket.HeadDirectoryBucket;
import com.example.s3.directorybucket.HeadDirectoryBucketObject;
import com.example.s3.directorybucket.ListDirectoryBucketMultipartUpload;
import com.example.s3.directorybucket.ListDirectoryBucketObjectsV2;
import com.example.s3.directorybucket.ListDirectoryBucketParts;
import com.example.s3.directorybucket.ListDirectoryBuckets;
import com.example.s3.directorybucket.PutDirectoryBucketEncryption;
import com.example.s3.directorybucket.PutDirectoryBucketObject;
import com.example.s3.directorybucket.UploadPartCopyForDirectoryBucket;
import com.example.s3.directorybucket.UploadPartForDirectoryBucket;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CompletedPart;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.MultipartUpload;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.Part;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.example.s3.directorybucket.GetDirectoryBucketEncryption.getDirectoryBucketEncryption;
import static com.example.s3.util.S3DirectoryBucketUtils.abortDirectoryBucketMultipartUploads;
import static com.example.s3.util.S3DirectoryBucketUtils.checkBucketExists;
import static com.example.s3.util.S3DirectoryBucketUtils.checkObjectExists;
import static com.example.s3.util.S3DirectoryBucketUtils.completeDirectoryBucketMultipartUpload;
import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.createDirectoryBucketMultipartUpload;
import static com.example.s3.util.S3DirectoryBucketUtils.createKmsKey;
import static com.example.s3.util.S3DirectoryBucketUtils.deleteAllObjectsInDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.doesBucketExist;
import static com.example.s3.util.S3DirectoryBucketUtils.getAwsAccountId;
import static com.example.s3.util.S3DirectoryBucketUtils.getBucketEncryptionType;
import static com.example.s3.util.S3DirectoryBucketUtils.getDirectoryBucketPolicy;
import static com.example.s3.util.S3DirectoryBucketUtils.getFilePath;
import static com.example.s3.util.S3DirectoryBucketUtils.multipartUploadForDirectoryBucket;
import static com.example.s3.util.S3DirectoryBucketUtils.putDirectoryBucketEncryption;
import static com.example.s3.util.S3DirectoryBucketUtils.putDirectoryBucketObject;
import static com.example.s3.util.S3DirectoryBucketUtils.putDirectoryBucketPolicy;
import static com.example.s3.util.S3DirectoryBucketUtils.scheduleKeyDeletion;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class S3DirectoryBucketIntegrationTest {
    // Logger for the class
    private static final Logger logger = LoggerFactory.getLogger(S3DirectoryBucketIntegrationTest.class);

    // AWS S3 client
    private static S3Client s3Client;

    // AWS account ID
    private static String awsAccountId = getAwsAccountId();

    // Region and Zone constants
    private static final Region region = Region.US_WEST_2;
    private static final String ZONE = "usw2-az1";

    // List to keep track of created buckets
    private static final List<String> createBuckets = new ArrayList<>();

    // Constants for bucket base names
    private static final String BUCKET_BASE_NAME = "test-bucket";
    private static final String SOURCE_BUCKET_BASE_NAME = "test-source-bucket";
    private static final String POLICY_BUCKET_BASE_NAME = "test-bucket-policy-bucket-name";

    // Generated bucket names for tests
    private static String testBucketName;
    private static String testSourceBucketName;
    private static String policyBucketName;

    // Policy text
    private static String policyText2;

    // Object keys
    private static String objectKey1 = "example-object-1";
    //private static String objectKey2 = "example-object-2";
    private static String mpuObject1 = "large-object-1";
    private static String mpuObject2 = "large-object-2";

    // File paths
    private static String localFilePath1 = "directoryBucket/sample1.txt";
    private static String localFilePath2 = "directoryBucket/sample2.txt";
    private static String localLargeFilePath = "directoryBucket/sample-large-object.jpg";
    private static Path filePath1 = getFilePath(localFilePath1);
    private static Path filePath2 = getFilePath(localFilePath2);
    private static Path filePathLarge = getFilePath(localLargeFilePath);

    // Static block to initialize time-dependent names
    static {
        long timestamp = System.currentTimeMillis();
        testBucketName = generateBucketName(BUCKET_BASE_NAME, timestamp);
        testSourceBucketName = generateBucketName(SOURCE_BUCKET_BASE_NAME, timestamp);
        policyBucketName = generateBucketName(POLICY_BUCKET_BASE_NAME, timestamp);
        policyText2 = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Sid\": \"AdminPolicy\",\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": {\n" +
                "                \"AWS\": \"arn:aws:iam::" + awsAccountId + ":root\"\n" +
                "            },\n" +
                "            \"Action\": \"s3express:*\",\n" +
                "            \"Resource\": \"arn:aws:s3express:us-west-2:" + awsAccountId + ":bucket/" + policyBucketName + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

    }

    // Helper method to generate bucket names
    private static String generateBucketName(String baseName, long timestamp) {
        return baseName + "-" + timestamp + "--" + ZONE + "--x-s3";
    }
    private static final KmsClient kmsClient = KmsClient.builder().region(region).build();
    private static String KMS_KEY_ID = null;

    @BeforeAll
    static void setup() {
        // Initialize the S3 client
        s3Client = S3Client.builder().region(region).build();
       

        // Log a message to verify logger configuration
        logger.info("Logger initialized and set to INFO level");

        // Create and set up buckets for various tests
        // Create a bucket for general testing
        createDirectoryBucket(s3Client, testBucketName, ZONE);
        createBuckets.add(testBucketName);

        // Create a source bucket for testing
        createDirectoryBucket(s3Client, testSourceBucketName, ZONE);
        createBuckets.add(testSourceBucketName);

        // Put objects to the source bucket for object level operation testing
        putDirectoryBucketObject(s3Client, testSourceBucketName, objectKey1, filePath1);
        //putDirectoryBucketObject(s3Client, testSourceBucketName, objectKey2, filePath2);

        // Create a bucket for policy testing
        createDirectoryBucket(s3Client, policyBucketName, ZONE);
        createBuckets.add(policyBucketName);

        KMS_KEY_ID = createKmsKey(kmsClient);

    }

    @Test
    @Tag("IntegrationTest")
    void testCreateBucket() {
        String newBucketName = "creation-" + testBucketName;
        try {
            CreateDirectoryBucket.createDirectoryBucket(s3Client, newBucketName, ZONE);
            createBuckets.add(newBucketName);
        } catch (RuntimeException rte) {
            logger.error("Failed to create bucket '{}': {}", newBucketName, rte.getMessage());
            throw rte; // Re-throw the exception to fail the test if bucket creation fails
        }

        // Check if the bucket exists
        Assertions.assertTrue(checkBucketExists(s3Client, testBucketName), "Bucket should exist after creation");
        logger.info("Test passed: Bucket '{}' exists", testBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testPutBucketPolicy() {
        // Initialize policy text with the generated bucket names
        String policyText = "{\n" +
                "    \"Version\": \"2012-10-17\",\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Sid\": \"AdminPolicy\",\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": {\n" +
                "                \"AWS\": \"arn:aws:iam::" + awsAccountId + ":root\"\n" +
                "            },\n" +
                "            \"Action\": \"s3express:*\",\n" +
                "            \"Resource\": \"arn:aws:s3express:us-west-2:" + awsAccountId + ":bucket/" + testBucketName + "\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        String appliedPolicy;
        JsonNode expectedPolicyJson;
        JsonNode appliedPolicyJson;
        try {
            // Apply the bucket policy
            putDirectoryBucketPolicy(s3Client, testBucketName, policyText);

            // Verify the policy was applied
            appliedPolicy = getDirectoryBucketPolicy(s3Client, testBucketName);

            // Parse policies using JSON
            ObjectMapper objectMapper = new ObjectMapper();
            expectedPolicyJson = objectMapper.readTree(policyText);
            appliedPolicyJson = objectMapper.readTree(appliedPolicy);
        } catch (RuntimeException | IOException e) {
            logger.error("An error occurred: {}", e.getMessage());
            throw new RuntimeException("Test failed due to an error", e);
        }

        // Compare the policies using JSON parsing
        Assertions.assertEquals(expectedPolicyJson, appliedPolicyJson, "Bucket policy should match the expected policy text");
        logger.info("Test passed: Bucket policy for '{}' is as expected", testBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testGetBucketPolicy() {
        String retrievedPolicy;
        JsonNode expectedPolicyJson;
        JsonNode retrievedPolicyJson;
        try {
            // Add a bucket policy to the bucket
            putDirectoryBucketPolicy(s3Client, policyBucketName, policyText2);

            // Verify the policy was applied
            retrievedPolicy = GetDirectoryBucketPolicy.getDirectoryBucketPolicy(s3Client, policyBucketName);
            logger.info("Retrieved policy: {}", retrievedPolicy);

            // Compare the policies using JSON parsing
            ObjectMapper objectMapper = new ObjectMapper();
            expectedPolicyJson = objectMapper.readTree(policyText2);
            retrievedPolicyJson = objectMapper.readTree(retrievedPolicy);
        } catch (RuntimeException | IOException e) {
            logger.error("An error occurred: {}", e.getMessage());
            throw new RuntimeException("Test failed due to an error", e);
        }

        Assertions.assertEquals(expectedPolicyJson, retrievedPolicyJson, "Bucket policy should match the expected policy text");
        logger.info("Test passed: Retrieved bucket policy for '{}' is as expected", policyBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testDeleteBucketPolicy() {
        String retrievedPolicy = null;
        try {
            // Apply the bucket policy
            putDirectoryBucketPolicy(s3Client, policyBucketName, policyText2);

            // Delete the bucket policy
            DeleteDirectoryBucketPolicy.deleteDirectoryBucketPolicy(s3Client, policyBucketName);

            // Verify the policy was deleted
            retrievedPolicy = getDirectoryBucketPolicy(s3Client, policyBucketName);

        } catch (RuntimeException rte) {
            if (retrievedPolicy != null) {
                logger.error("Bucket policy was not deleted for '{}': {}", policyBucketName, rte.getMessage());
                throw rte;
            } else {
                logger.warn("No policy found for bucket '{}', which is expected after deletion.", policyBucketName);
            }
        }

        Assertions.assertNull(retrievedPolicy, "Bucket policy should be null after deletion");
        logger.info("Test passed: Bucket policy for '{}' was deleted as expected", policyBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testPutBucketEncryption() {
        String encryptionType;
        long timestamp = System.currentTimeMillis();
        final String PUT_ENCRYPTION_BUCKET_BASE_NAME = "test-put-encrypt-bucket-name";
        String testPutEncryptionBucketName = generateBucketName(PUT_ENCRYPTION_BUCKET_BASE_NAME, timestamp);
        // Create a bucket for SSE-KMS encryption testing
        createDirectoryBucket(s3Client, testPutEncryptionBucketName, ZONE);
        createBuckets.add(testPutEncryptionBucketName);

        try {
            // Set bucket encryption using the KMS key
            PutDirectoryBucketEncryption.putDirectoryBucketEncryption(s3Client, testPutEncryptionBucketName, KMS_KEY_ID);

            // Verify the encryption type of the bucket
            encryptionType = getBucketEncryptionType(s3Client, testPutEncryptionBucketName);
        } catch (RuntimeException e) {
            logger.error("An error occurred during bucket encryption or KMS key handling: {}", e.getMessage());
            throw e;
        }

        Assertions.assertEquals("aws:kms", encryptionType, "Bucket should be encrypted with aws:kms");
        logger.info("Test passed: Put bucket encryption successfully for '{}'", testPutEncryptionBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testGetBucketEncryption() {
        String encryptionType;

        final String GET_ENCRYPTION_BUCKET_BASE_NAME = "test-get-encrypt-bucket-name";
        String testGetEncryptionBucketName;
        long timestamp = System.currentTimeMillis();
        testGetEncryptionBucketName = generateBucketName(GET_ENCRYPTION_BUCKET_BASE_NAME, timestamp);
        // Create a bucket for get encryption testing
        createDirectoryBucket(s3Client, testGetEncryptionBucketName, ZONE);
        createBuckets.add(testGetEncryptionBucketName);

        try {
            // Retrieve the bucket encryption type
            encryptionType = getDirectoryBucketEncryption(s3Client, testGetEncryptionBucketName);
        } catch (RuntimeException e) {
            logger.error("Failed to get bucket encryption for '{}': {}", testGetEncryptionBucketName, e.getMessage());
            throw e;
        }
    // Verify the encryption type. By default, S3 directory buckets are encrypted with AES256 when no other encryption types are specified.
        Assertions.assertEquals("AES256", encryptionType, "Bucket encryption should be AES256");
        logger.info("Test passed: Got bucket encryption successfully for '{}'", testGetEncryptionBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testDeleteBucketEncryption() {
        String encryptionTypeAfterDeletion;
        long timestamp = System.currentTimeMillis();
        final String DELETE_ENCRYPTION_BUCKET_BASE_NAME = "test-delete-encrypt-bucket-name";
        String testDeleteEncryptionBucketName;
                    testDeleteEncryptionBucketName = generateBucketName(DELETE_ENCRYPTION_BUCKET_BASE_NAME, timestamp);
        // Create a bucket for delete encryption testing
        createDirectoryBucket(s3Client, testDeleteEncryptionBucketName, ZONE);
        createBuckets.add(testDeleteEncryptionBucketName);

        try {
            // Set the bucket encryption to SSE-KMS
            putDirectoryBucketEncryption(s3Client, testDeleteEncryptionBucketName, KMS_KEY_ID);

            // Delete the bucket encryption
            DeleteDirectoryBucketEncryption.deleteDirectoryBucketEncryption(s3Client, testDeleteEncryptionBucketName);

            // Verify the encryption type after deletion
            encryptionTypeAfterDeletion = getDirectoryBucketEncryption(s3Client, testDeleteEncryptionBucketName);
        } catch (RuntimeException e) {
            logger.error("Failed to delete bucket encryption for '{}': {}", testDeleteEncryptionBucketName, e.getMessage());
            throw e;
        }

        // By default, S3 buckets should be encrypted with AES256 if no other encryption is specified
        Assertions.assertEquals("AES256", encryptionTypeAfterDeletion, "Bucket encryption should be AES256 after deletion");
        logger.info("Test passed: Deleted bucket encryption successfully for '{}'", testDeleteEncryptionBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testListDirectoryBuckets() {
        List<String> bucketNames;

        try {
            // List directory buckets
            bucketNames = ListDirectoryBuckets.listDirectoryBuckets(s3Client);
            logger.info("List directory buckets '{}'", bucketNames);
        } catch (RuntimeException e) {
            logger.error("Failed to list directory buckets: {}", e.getMessage());
            throw e;
        }

        // Verify that the bucket list is not empty. There are already some buckets created in the setup, so the list should not be empty.
        Assertions.assertFalse(bucketNames.isEmpty(), "The list of directory buckets should not be empty");
        logger.info("Test passed: Listed directory buckets successfully");
    }


    @Test
    @Tag("IntegrationTest")
    void testHeadBucket() {
        boolean bucketExists;

        try {
            // Perform the head bucket operation
            bucketExists = HeadDirectoryBucket.headDirectoryBucket(s3Client, testBucketName);
        } catch (RuntimeException e) {
            logger.error("Failed to perform head bucket check for '{}': {}", testBucketName, e.getMessage());
            throw e;
        }

        // Verify the bucket exists
        Assertions.assertTrue(bucketExists, "The bucket should exist");
        logger.info("Test passed: Head bucket check successfully for '{}'", testBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testDeleteBucket() {
        boolean bucketDeleted;
        final String EXCEPTION_BUCKET_BASE_NAME = "exception-bucket-name";
        String exceptionBucketName;
        long timestamp = System.currentTimeMillis();
        exceptionBucketName = generateBucketName(EXCEPTION_BUCKET_BASE_NAME, timestamp);
        createDirectoryBucket(s3Client, exceptionBucketName, ZONE);
        createBuckets.add(exceptionBucketName);

        try {
            // The testing bucket is empty. Delete the bucket.
            DeleteDirectoryBucket.deleteDirectoryBucket(s3Client, exceptionBucketName);

            // Verify the bucket has been deleted
            bucketDeleted = !doesBucketExist(s3Client, exceptionBucketName);
        } catch (RuntimeException e) {
            logger.error("Failed to delete bucket for '{}': {}", exceptionBucketName, e.getMessage());
            throw e;
        }

        // Verify that the bucket no longer exists
        Assertions.assertTrue(bucketDeleted, "The bucket should be deleted");
        logger.info("Test passed: Deleted bucket successfully for '{}'", exceptionBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testPutObject() {
        boolean objectExists;

        try {
            // Put the object into the bucket
            PutDirectoryBucketObject.putDirectoryBucketObject(s3Client, testBucketName, objectKey1, filePath1);

            // Check if the object exists
            objectExists = checkObjectExists(s3Client, testBucketName, objectKey1);
        } catch (RuntimeException e) {
            logger.error("Failed to put object into '{}': {}", testBucketName, e.getMessage());
            throw e;
        }

        // Verify the object exists in the bucket
        Assertions.assertTrue(objectExists, "The object should exist in the bucket after being put");
        logger.info("Test passed: Put object successfully into '{}'", testBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testCopyObject() {
        boolean objectExists ;
        long timestamp = System.currentTimeMillis();
        final String DESTINATION_BUCKET_BASE_NAME = "test-destination-bucket";
        String testDestinationBucketName;
        testDestinationBucketName = generateBucketName(DESTINATION_BUCKET_BASE_NAME, timestamp);
        // Create a destination bucket for copy testing
        createDirectoryBucket(s3Client, testDestinationBucketName, ZONE);
        createBuckets.add(testDestinationBucketName);

        try {
            // Copy the object to the destination bucket
            CopyDirectoryBucketObject.copyDirectoryBucketObject(s3Client, testSourceBucketName, objectKey1, testDestinationBucketName);

            // Check if the object exists in the destination bucket
            objectExists = checkObjectExists(s3Client, testDestinationBucketName, objectKey1);
        } catch (RuntimeException e) {
            logger.error("Failed to copy object from '{}' to '{}': {}", testSourceBucketName, testDestinationBucketName, e.getMessage());
            throw e;
        }

        // Verify the object exists in the destination bucket
        Assertions.assertTrue(objectExists, "The object should exist in the destination bucket after being copied");
        logger.info("Test passed: Copied object successfully from '{}' to '{}'", testSourceBucketName, testDestinationBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testListObjectsV2() {
        List<String> objectKeys ;

        try {
            // In set up, one object is put into the bucket: testSourceBucketName.
            // List objects in the bucket
            objectKeys = ListDirectoryBucketObjectsV2.listDirectoryBucketObjectsV2(s3Client, testSourceBucketName);
        } catch (RuntimeException e) {
            logger.error("Failed to list objects in '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }

        // Verify that the object list is not empty. There should be at least one object in the bucket.
        Assertions.assertFalse(objectKeys.isEmpty(), "The list of objects in the bucket should not be empty");
        logger.info("Test passed: Listed objects in bucket '{}'", testSourceBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testGetObject() {
        boolean objectRetrieved;

        try {
            // Retrieve the object from the bucket
            objectRetrieved = GetDirectoryBucketObject.getDirectoryBucketObject(s3Client, testSourceBucketName, objectKey1);
        } catch (RuntimeException e) {
            logger.error("Failed to retrieve object from '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }

        // Verify the object was retrieved successfully
        Assertions.assertTrue(objectRetrieved, "The object should be successfully retrieved from the bucket");
        logger.info("Test passed: Retrieved object successfully from '{}'", testSourceBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testHeadObject() {
        boolean objectExists;

        try {
            // Perform the head object operation
            objectExists = HeadDirectoryBucketObject.headDirectoryBucketObject(s3Client, testSourceBucketName, objectKey1);
        } catch (RuntimeException e) {
            logger.error("Failed to perform head object check for '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }

        // Verify the object exists
        Assertions.assertTrue(objectExists, "The object should exist in the bucket");
        logger.info("Test passed: Head object check successfully for '{}'", testSourceBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testGetObjectAttributes() {
        boolean attributesRetrieved;

        try {
            // Retrieve the object attributes from the bucket
            attributesRetrieved = GetDirectoryBucketObjectAttributes.getDirectoryBucketObjectAttributes(s3Client, testSourceBucketName, objectKey1);
        } catch (RuntimeException e) {
            logger.error("Failed to retrieve object attributes from '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }

        // Verify the object attributes were retrieved successfully
        Assertions.assertTrue(attributesRetrieved, "The object attributes should be successfully retrieved from the bucket");
        logger.info("Test passed: Retrieved object attributes successfully from '{}'", testSourceBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testGeneratePresignedGetURL() {
        // AWS S3 presigner
        S3Presigner s3Presigner = S3Presigner.builder().region(region).build();
        boolean urlGenerated = false;
        boolean urlAccessible = false;

        try {
            // Generate the presigned GET URL
            String presignedURL = GeneratePresignedGetURLForDirectoryBucket.generatePresignedGetURLForDirectoryBucket(s3Presigner, testSourceBucketName, objectKey1);

            // Check if the presigned URL is not null
            if (presignedURL != null) {
                urlGenerated = true;
                // Make an HTTP GET request to the presigned URL
                HttpURLConnection connection = (HttpURLConnection) new URL(presignedURL).openConnection();
                connection.setRequestMethod("GET");

                // Verify the HTTP response status code
                int responseCode = connection.getResponseCode();
                urlAccessible = (responseCode == 200);

                connection.disconnect();
            }
        } catch (RuntimeException | IOException e) {
            logger.error("Failed to generate or access presigned GET URL for '{}': {}", testSourceBucketName, e.getMessage());
            throw new RuntimeException("Test failed due to an error", e);
        }

        // Verify the presigned GET URL was generated and is accessible
        Assertions.assertTrue(urlGenerated, "The presigned GET URL should be generated successfully");
        Assertions.assertTrue(urlAccessible, "The presigned GET URL should be accessible");
        logger.info("Test passed: Generated and accessed presigned GET URL successfully for '{}'", testSourceBucketName);
    }

    @Test
    @Tag("IntegrationTest")
    void testDeleteObject() {
        String objectKeyDelete1 = "example-object-1-delete";
        boolean objectExistsBeforeDeletion;
        boolean objectDeleted;

        try {
            // Put an object into the bucket for testing deletion
            putDirectoryBucketObject(s3Client, testSourceBucketName, objectKeyDelete1, filePath1);

            // Verify the object exists before deletion
            objectExistsBeforeDeletion = checkObjectExists(s3Client, testSourceBucketName, objectKeyDelete1);

            // Delete the object
            DeleteDirectoryBucketObject.deleteDirectoryBucketObject(s3Client, testSourceBucketName, objectKeyDelete1);

            // Verify the object is deleted
            objectDeleted = !checkObjectExists(s3Client, testSourceBucketName, objectKeyDelete1);
        } catch (RuntimeException e) {
            logger.error("Failed to delete object from '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }

        // Verify the object existed before deletion and was successfully deleted
        Assertions.assertTrue(objectExistsBeforeDeletion, "The object should exist in the bucket before deletion");
        Assertions.assertTrue(objectDeleted, "The object should be successfully deleted from the bucket");
        logger.info("Test passed: Deleted object successfully from '{}'", testSourceBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testDeleteObjects() {
        final String DELETE_OBJECTS_BUCKET_BASE_NAME = "test-delete-objects-bucket-name";
        String testDeleteObjectsBucketName;
        // Create buckets and put objects for exception and DeleteObjects testing
        long timestamp = System.currentTimeMillis();
        testDeleteObjectsBucketName = generateBucketName(DELETE_OBJECTS_BUCKET_BASE_NAME, timestamp);
        createDirectoryBucket(s3Client, testDeleteObjectsBucketName, ZONE);
        createBuckets.add(testDeleteObjectsBucketName);
        String objectKeyDelete1 = "example-object-1-delete";
        String objectKeyDelete2 = "example-object-2-delete";
        boolean objectsExistBeforeDeletion;
        boolean objectsDeleted;

        try {
            putDirectoryBucketObject(s3Client, testDeleteObjectsBucketName, objectKeyDelete1, filePath1);
            putDirectoryBucketObject(s3Client, testDeleteObjectsBucketName, objectKeyDelete2, filePath2);

            // Verify the objects exist before deletion
            objectsExistBeforeDeletion = checkObjectExists(s3Client, testDeleteObjectsBucketName, objectKeyDelete1) &&
                    checkObjectExists(s3Client, testDeleteObjectsBucketName, objectKeyDelete2);

            // Delete the objects
            DeleteDirectoryBucketObjects.deleteDirectoryBucketObjects(s3Client, testDeleteObjectsBucketName,
                    List.of(objectKeyDelete1, objectKeyDelete2));

            // Verify the objects are deleted
            objectsDeleted = !checkObjectExists(s3Client, testDeleteObjectsBucketName, objectKeyDelete1) &&
                    !checkObjectExists(s3Client, testDeleteObjectsBucketName, objectKeyDelete2);
        } catch (RuntimeException e) {
            logger.error("Failed to delete objects from '{}': {}", testDeleteObjectsBucketName, e.getMessage());
            throw e;
        }

        // Verify the objects existed before deletion and were successfully deleted
        Assertions.assertTrue(objectsExistBeforeDeletion, "The objects should exist in the bucket before deletion");
        Assertions.assertTrue(objectsDeleted, "The objects should be successfully deleted from the bucket");
        logger.info("Test passed: Deleted multiple objects successfully from '{}'", testDeleteObjectsBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testCreateMultipartUpload() {
        String uploadId ;

        try {
            // Create a multipart upload
            uploadId = CreateDirectoryBucketMultipartUpload.createDirectoryBucketMultipartUpload(s3Client, testSourceBucketName, mpuObject1);
        } catch (RuntimeException e) {
            logger.error("Failed to create multipart upload for '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }

        // Verify the multipart upload was created successfully
        Assertions.assertNotNull(uploadId, "The multipart upload should be created successfully");
        logger.info("Test passed: Created multipart upload successfully for '{}'", testSourceBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testUploadPart() throws IOException {
        final String MPU_BUCKET_BASE_NAME = "test-mpu-bucket-name";
        long timestamp = System.currentTimeMillis();
        String testMPUBucketName = generateBucketName(MPU_BUCKET_BASE_NAME, timestamp);
        List<CompletedPart> completedParts ;

        // Create the bucket and multipart upload
        createDirectoryBucket(s3Client, testMPUBucketName, ZONE);
        createBuckets.add(testMPUBucketName);
        String uploadId1 = createDirectoryBucketMultipartUpload(s3Client, testMPUBucketName, mpuObject1);

        try {
            // Upload a part for the multipart upload
            completedParts = UploadPartForDirectoryBucket.multipartUploadForDirectoryBucket(s3Client, testMPUBucketName, mpuObject1, uploadId1, filePathLarge);
        } catch (RuntimeException | IOException e) {
            logger.error("Failed to upload part for '{}': {}", testMPUBucketName, e.getMessage());
            throw e;
        }

        // Verify the part was uploaded successfully
        Assertions.assertFalse(completedParts.isEmpty(), "The part should be uploaded successfully");
        logger.info("Test passed: Uploaded part successfully for '{}'", testMPUBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testUploadPartCopy() throws IOException {
        String uploadIdSourceMPU;
        String uploadIdDestinationMPU;
        List<CompletedPart> uploadedPartsListSource ;
        List<CompletedPart> copiedPartsListDestination ;
        boolean copySuccessful = false;

        // Create a multipart upload of source bucket for testing
        uploadIdSourceMPU = createDirectoryBucketMultipartUpload(s3Client, testSourceBucketName, mpuObject1);

        // Perform multipart upload in the source directory bucket for testing
        uploadedPartsListSource = multipartUploadForDirectoryBucket(s3Client, testSourceBucketName, mpuObject1, uploadIdSourceMPU, filePathLarge);

        // Complete Multipart Uploads
        completeDirectoryBucketMultipartUpload(s3Client, testSourceBucketName, mpuObject1, uploadIdSourceMPU, uploadedPartsListSource);

        // Create a multipart upload of destination bucket for testing
        uploadIdDestinationMPU = createDirectoryBucketMultipartUpload(s3Client, testBucketName, mpuObject2);

        try {
            // Perform multipart upload copy in the destination directory bucket
            copiedPartsListDestination = UploadPartCopyForDirectoryBucket.multipartUploadCopyForDirectoryBucket(s3Client, testSourceBucketName, mpuObject1, testBucketName, mpuObject2, uploadIdDestinationMPU);

            // Check if the copy was successful by verifying if copiedPartsListDestination is not null or empty
            if (copiedPartsListDestination != null && !copiedPartsListDestination.isEmpty()) {
                copySuccessful = true;
            }
        } catch (RuntimeException e) {
            logger.error("Failed to copy part for the object '{}': {}", mpuObject2, e.getMessage());
            throw e;
        }

        // Verify the part was copied successfully
        Assertions.assertTrue(copySuccessful, "The part should be copied successfully");
        logger.info("Test passed: Copied part successfully for the object copy '{}' to the destination bucket '{}'", mpuObject2, testBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testListParts() throws IOException {
        // Create a multipart upload for ListParts and ListMultipartUpload testing
        String uploadId2 = createDirectoryBucketMultipartUpload(s3Client, testBucketName, mpuObject1);

        // Perform multipart upload in the directory bucket for testing
        multipartUploadForDirectoryBucket(s3Client, testBucketName, mpuObject1, uploadId2, filePathLarge);

        List<Part> parts;

        try {
            // List the parts of the multipart upload
            parts = ListDirectoryBucketParts.listDirectoryBucketMultipartUploadParts(s3Client, testBucketName, mpuObject1, uploadId2);
        } catch (RuntimeException e) {
            logger.error("Failed to list parts for '{}': {}", testBucketName, e.getMessage());
            throw e;
        }

        // Verify the parts were listed successfully
        Assertions.assertFalse(parts.isEmpty(), "The parts should be listed successfully");
        parts.forEach(part -> logger.info("Part Number: {}, ETag: {}, Size: {}", part.partNumber(), part.eTag(), part.size()));
        logger.info("Test passed: Listed parts successfully for '{}'", testBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testListMultipartUpload() throws IOException {
        // Create a multipart upload for testing
        String uploadId = createDirectoryBucketMultipartUpload(s3Client, testBucketName, mpuObject1);

        // Perform multipart upload in the directory bucket for testing
        multipartUploadForDirectoryBucket(s3Client, testBucketName, mpuObject1, uploadId, filePathLarge);

        List<MultipartUpload> multipartUploads;

        try {
            // List the multipart uploads in the bucket
            multipartUploads = ListDirectoryBucketMultipartUpload.listDirectoryBucketMultipartUploads(s3Client, testBucketName);

        } catch (RuntimeException e) {
            logger.error("Failed to list multipart uploads for '{}': {}", testBucketName, e.getMessage());
            throw e;
        }

        // Verify the multipart uploads were listed successfully
        Assertions.assertFalse(multipartUploads.isEmpty(), "The multipart uploads should be listed successfully");
        multipartUploads.forEach(upload -> logger.info("Upload ID: {}, Key: {}", upload.uploadId(), upload.key()));
        logger.info("Test passed: Listed multipart uploads successfully for '{}'", testBucketName);
    }



    @Test
    @Tag("IntegrationTest")
    void testCompleteMultipartUpload() throws IOException {
        String uploadId3;

        // Create a multipart upload for testing
        uploadId3 = createDirectoryBucketMultipartUpload(s3Client, testBucketName, mpuObject2);
        // Perform multipart upload in the directory bucket for testing
        List<CompletedPart> uploadedPartsList = multipartUploadForDirectoryBucket(s3Client, testBucketName, mpuObject2, uploadId3, filePathLarge);
        Integer numUploadsBeforeComplete = s3Client.listMultipartUploads(b -> b.bucket(testBucketName)).uploads().size();

        try {
            // Complete the multipart upload
            CompleteDirectoryBucketMultipartUpload.completeDirectoryBucketMultipartUpload(s3Client, testBucketName, mpuObject2, uploadId3, uploadedPartsList);
        } catch (RuntimeException e) {
            logger.error("Failed to complete multipart upload for '{}': {}", testBucketName, e.getMessage());
            throw e;
        }
        Integer numUploadsAfterComplete = s3Client.listMultipartUploads(b -> b.bucket(testBucketName)).uploads().size();

        // Verify the multipart upload was completed successfully
        Assertions.assertEquals(1, numUploadsBeforeComplete - numUploadsAfterComplete, "The multipart upload should be completed successfully");
        logger.info("Test passed: Completed multipart upload successfully for '{}'", testBucketName);
    }


    @Test
    @Tag("IntegrationTest")
    void testAbortMultipartUpload() {
        String uploadId4;
        // Create a multipart upload for testing
        uploadId4 = createDirectoryBucketMultipartUpload(s3Client, testSourceBucketName, mpuObject1);
        Integer numUploadsBeforeAbort = s3Client.listMultipartUploads(b -> b.bucket(testSourceBucketName)).uploads().size();

        try {
            // Abort the multipart upload
            AbortDirectoryBucketMultipartUploads.abortDirectoryBucketMultipartUpload(s3Client, testSourceBucketName, mpuObject1, uploadId4);
        } catch (RuntimeException e) {
            logger.error("Failed to abort multipart upload for '{}': {}", testSourceBucketName, e.getMessage());
            throw e;
        }
        Integer numUploadsAfterAbort = s3Client.listMultipartUploads(b -> b.bucket(testSourceBucketName)).uploads().size();
        // Verify the multipart upload was aborted successfully
        Assertions.assertEquals(1, numUploadsBeforeAbort - numUploadsAfterAbort, "The multipart upload should be aborted successfully");
        logger.info("Test passed: Aborted multipart upload successfully for '{}'", testSourceBucketName);
    }


    @AfterAll
    static void teardown() {

        // Empty and delete the S3 buckets created for testing
        for (String bucketName : createBuckets) {
            try {
                // Delete all objects in the bucket
                deleteAllObjectsInDirectoryBucket(s3Client, bucketName);

                // Abort multipart uploads
                abortDirectoryBucketMultipartUploads(s3Client, bucketName);

                // Ensure the bucket exists before attempting to delete it
                if (doesBucketExist(s3Client, bucketName)) {
                    // Delete the bucket
                    DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                            .bucket(bucketName)
                            .build();
                    s3Client.deleteBucket(deleteBucketRequest);
                    logger.info("Bucket deleted: " + bucketName);
                } else {
                    logger.warn("Bucket does not exist and cannot be deleted: " + bucketName);
                }
            } catch (NoSuchBucketException e) {
                logger.error("Failed to delete objects in bucket: " + bucketName + " - Error code: NoSuchBucket", e);
            } catch (S3Exception e) {
                if (e.awsErrorDetails().errorCode().contains("KMS.KMSInvalidStateException")) {
                    logger.warn("Bucket: {} could not be deleted due to KMS key pending deletion. Error: {}", bucketName, e.awsErrorDetails().errorMessage());
                } else {
                    logger.error("Failed to delete objects in bucket: " + bucketName + " - Error code: " + e.awsErrorDetails().errorCode(), e);
                }
            } catch (Exception e) {
                logger.error("Failed to delete bucket: " + bucketName, e);
            }
        }

        // Schedule the deletion of the created KMS key
        if (KMS_KEY_ID != null) {
            try {
                String deletionDate = scheduleKeyDeletion(kmsClient, KMS_KEY_ID, 7); // 7 days waiting period
                logger.info("Key scheduled for deletion on: {}", deletionDate);
            } catch (RuntimeException e) {
                logger.error("Failed to schedule key deletion: {}", e.getMessage());
            }
        }

        // Ensure the KMS client is closed
        kmsClient.close();

        // Close the S3 client
        s3Client.close();
    }


}