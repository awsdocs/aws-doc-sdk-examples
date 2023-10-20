/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.example.s3.CopyObject;
import com.example.s3.CopyObjectStorage;
import com.example.s3.CreateAccessPoint;
import com.example.s3.CreateBucket;
import com.example.s3.DeleteBucketPolicy;
import com.example.s3.DeleteMultiObjects;
import com.example.s3.DeleteObjects;
import com.example.s3.GetObjectData;
import com.example.s3.GetObjectPresignedUrl;
import com.example.s3.GetObjectRestoreStatus;
import com.example.s3.LifecycleConfiguration;
import com.example.s3.ListObjects;
import com.example.s3.PutObject;
import com.example.s3.RestoreObject;
import com.example.s3.S3Cors;
import com.example.s3.S3Scenario;
import com.example.s3.S3ZipExample;
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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

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
        bucketName = values.getBucketName()+ java.util.UUID.randomUUID();
        objectKey = values.getObjectKey();
        objectPath= values.getObjectPath();
        toBucket = values.getToBucket();
        policyText = values.getPolicyText();
        id = values.getId();
        presignKey = values.getPresignKey();
        presignBucket= values.getPresignBucket();
        path = values.getPath();
        bucketNamePolicy = values.getBucketNamePolicy();
        accountId = values.getAccountId();
        accessPointName = values.getAccessPointName();
        encryptObjectName = values.getEncryptObjectName();
        encryptObjectPath = values.getEncryptObjectPath();
        encryptOutPath = values.getEncryptOutPath();
        keyId = values.getKeyId();
        restoreImagePath = values.getRestoreImagePath();
        restoreBucket = values.getRestoreBucket();
        restoreImageName = values.getRestoreImageName();
        bucketNameSc = values.getBucketNameSc()+ java.util.UUID.randomUUID();
        keySc = values.getKeySc();
        objectPathSc = values.getObjectPathSc();
        savePathSc = values.getSavePathSc();
        toBucketSc = values.getToBucketSc();
        bucketNameZip = values.getBucketNameZip();
        images = values.getImages();
        imageKeys = images.split("[,]", 0);

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonS3Test.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();

            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            bucketName = prop.getProperty("bucketName")+ java.util.UUID.randomUUID();;
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
            bucketNameSc = prop.getProperty("bucketNameSc")+ java.util.UUID.randomUUID();;
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
        */
    }

   @Test
   @Tag("IntegrationTest")
   @Order(1)
   public void createBucket() {
       assertDoesNotThrow(() ->CreateBucket.createBucket(s3,bucketName));
       System.out.println("Test 1 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(2)
   public void putObject() {
       assertDoesNotThrow(() ->PutObject.putS3Object(s3, bucketName, objectKey, objectPath));
       System.out.println("Test 2 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(3)
   public void copyBucketObject() {
       String result = CopyObject.copyBucketObject(s3,bucketName,objectKey,toBucket);
       assertFalse(result.isEmpty());
       System.out.println("Test 3 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void deleteBucketPolicy() {
        assertDoesNotThrow(() ->DeleteBucketPolicy.deleteS3BucketPolicy(s3,bucketNamePolicy));
        System.out.println("Test 6 passed");
    }


    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void getObjectPresignedUrl() {
        assertDoesNotThrow(() ->GetObjectPresignedUrl.getPresignedUrl(presigner, presignBucket, presignKey));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void getObjectData() {
        assertDoesNotThrow(() ->GetObjectData.getObjectBytes(s3,bucketName,objectKey, path));
        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void listObjects() {
        assertDoesNotThrow(() ->ListObjects.listBucketObjects(s3,bucketName));
        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void createAccessPoint() {
        assertDoesNotThrow(() ->CreateAccessPoint.createSpecificAccessPoint(s3ControlClient, accountId, bucketName, accessPointName));
        assertDoesNotThrow(() ->CreateAccessPoint.deleteSpecificAccessPoint(s3ControlClient, accountId, accessPointName));
        System.out.println("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void lifecycleConfiguration() {
        assertDoesNotThrow(() ->LifecycleConfiguration.setLifecycleConfig(s3, bucketName, accountId));
        assertDoesNotThrow(() ->LifecycleConfiguration.getLifecycleConfig(s3, bucketName, accountId));
        assertDoesNotThrow(() ->LifecycleConfiguration.deleteLifecycleConfig(s3, bucketName, accountId));
        System.out.println("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void s3Cors() {
        assertDoesNotThrow(() ->S3Cors.setCorsInformation(s3, bucketName, accountId));
        assertDoesNotThrow(() ->S3Cors.getBucketCorsInformation(s3, bucketName, accountId));
        assertDoesNotThrow(() ->S3Cors.deleteBucketCorsInformation(s3, bucketName, accountId));
        System.out.println("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void deleteMultiObjects() {
        assertDoesNotThrow(() ->DeleteMultiObjects.deleteBucketObjects(s3, bucketName));
        System.out.println("Test 14 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void deleteObjects() {
        assertDoesNotThrow(() ->DeleteObjects.deleteBucketObjects(s3,bucketName,objectKey));
        assertDoesNotThrow(() ->DeleteObjects.deleteBucketObjects(s3,bucketName,encryptObjectName));
        System.out.println("Test 15 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void copyObjectStorage() {
        assertDoesNotThrow(() ->PutObject.putS3Object(s3, restoreBucket, restoreImageName, restoreImagePath));
        assertDoesNotThrow(() ->CopyObjectStorage.copyBucketObject(s3,restoreBucket, restoreImageName, restoreBucket));
        System.out.println("Test 16 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void restoreObject() {
        assertDoesNotThrow(() ->RestoreObject.restoreS3Object(s3, restoreBucket, restoreImageName, accountId));
        System.out.println("Test 17 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(17)
    public void getRestoreStatus() {
        assertDoesNotThrow(() ->GetObjectRestoreStatus.checkStatus(s3, restoreBucket, restoreImageName));
        System.out.println("Test 18 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(18)
    public void s3ZipExample() {
        assertDoesNotThrow(() ->S3ZipExample.createZIPFile(s3, bucketNameZip, imageKeys));
        System.out.println("Test 19 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(19)
    public void deleteBucket() {
        assertDoesNotThrow(() ->S3Scenario.deleteBucket(s3, bucketName));
        System.out.println("Test 19 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(20)
    public void testScenario() {
        System.out.println(S3Scenario.DASHES);
        System.out.println("1. Create an Amazon S3 bucket.");
        assertDoesNotThrow(() ->S3Scenario.createBucket(s3, bucketNameSc));
        System.out.println( S3Scenario.DASHES);

        System.out.println( S3Scenario.DASHES);
        System.out.println("2. Update a local file to the Amazon S3 bucket.");
        assertDoesNotThrow(() ->S3Scenario.uploadLocalFile(s3, bucketNameSc, keySc, objectPathSc));
        System.out.println(S3Scenario.DASHES);

        System.out.println( S3Scenario.DASHES);
        System.out.println("3. Download the object to another local file.");
        assertDoesNotThrow(() ->S3Scenario.getObjectBytes (s3, bucketNameSc, keySc, savePathSc));
        System.out.println( S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("4. Perform a multipart upload.");
        String multipartKey = "multiPartKey";
        assertDoesNotThrow(() ->S3Scenario.multipartUpload(s3, toBucketSc, multipartKey));
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("5. List all objects located in the Amazon S3 bucket.");
        assertDoesNotThrow(() ->S3Scenario.listAllObjects(s3, bucketNameSc));
        assertDoesNotThrow(() ->S3Scenario.anotherListExample(s3, bucketNameSc)) ;
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("6. Copy the object to another Amazon S3 bucket.");
        assertDoesNotThrow(() ->S3Scenario.copyBucketObject (s3, bucketNameSc, keySc, toBucketSc));
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("7. Delete the object from the Amazon S3 bucket.");
        assertDoesNotThrow(() ->S3Scenario.deleteObjectFromBucket(s3, bucketNameSc, keySc));
        System.out.println(S3Scenario.DASHES);

        System.out.println(S3Scenario.DASHES);
        System.out.println("8. Delete the Amazon S3 bucket.");
        assertDoesNotThrow(() ->S3Scenario.deleteBucket(s3, bucketNameSc));
        System.out.println(S3Scenario.DASHES);

        System.out.println("Test 20 passed");
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

