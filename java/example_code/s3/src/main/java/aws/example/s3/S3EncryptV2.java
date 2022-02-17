//snippet-sourcedescription:[S3EncryptV2.java demonstrates how to encrypt S3 content by using the AmazonS3EncryptionV2 object]
//snippet-keyword:[SDK for Java]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon DynamoDB]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/6/2020]
//snippet-sourceauthor:[scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package aws.example.s3;

// snippet-start:[s3.java1.s3_encryptv2.import]
import com.amazonaws.ClientConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.CreateKeyRequest;
import com.amazonaws.services.kms.model.CreateKeyResult;
import com.amazonaws.services.kms.model.ScheduleKeyDeletionRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClientV2Builder;
import com.amazonaws.services.s3.AmazonS3EncryptionV2;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CryptoConfigurationV2;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
// snippet-end:[s3.java1.s3_encryptv2.import]

public class S3EncryptV2 {

    // specify S3 bucket name to create and use for testing this example
    public static final String bucketName = "s3encryptionclient-" + Math.random();

    public static void main(String[] args) throws NoSuchAlgorithmException {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        // create a bucket for testing; will be deleted automatically upon successful completion
        // of this example
        try {
            s3Client.createBucket(bucketName);
        } catch (AmazonS3Exception e) {
            System.err.println(e.getErrorMessage());
        }

        putEncryptedData1();
        putEncryptedData2();
        putEncryptedData3_Kms();

        try {
            ObjectListing objectListing = s3Client.listObjects(bucketName);
            while (true) {
                for (Iterator<?> iterator =
                     objectListing.getObjectSummaries().iterator();
                     iterator.hasNext(); ) {
                    S3ObjectSummary summary = (S3ObjectSummary) iterator.next();
                    s3Client.deleteObject(bucketName, summary.getKey());
                }
                if (objectListing.isTruncated()) {
                    objectListing = s3Client.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }

        } catch (AmazonS3Exception e) {
            System.err.println(e.getErrorMessage());
        }

        // delete test bucket
        try {
            s3Client.deleteBucket(bucketName);
        } catch (AmazonS3Exception e) {
            System.err.println(e.getErrorMessage());
        }
        s3Client.shutdown();
        System.out.println("Done");
    }

    public static void putEncryptedData1() throws NoSuchAlgorithmException {

        // snippet-start:[s3.java.s3_cse_v2.symmetric]
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);

        // generate a symmetric encryption key for testing
        SecretKey secretKey = keyGenerator.generateKey();

        // snippet-start:[s3.java.s3_cse_v2.strictauth]
        String s3ObjectKey = "EncryptedContent1.txt";
        String s3ObjectContent = "This is the 1st content to encrypt";

        AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard()
                .withRegion(Regions.DEFAULT_REGION)
                .withClientConfiguration(new ClientConfiguration())
                .withCryptoConfiguration(new CryptoConfigurationV2().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterialsProvider(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
                .build();

        s3Encryption.putObject(bucketName, s3ObjectKey, s3ObjectContent);
        // snippet-end:[s3.java.s3_cse_v2.strictauth]
        System.out.println(s3Encryption.getObjectAsString(bucketName, s3ObjectKey));
        s3Encryption.shutdown();
        // snippet-end:[s3.java.s3_cse_v2.symmetric]
    }

    public static void putEncryptedData2() throws NoSuchAlgorithmException {

        // snippet-start:[s3.java.s3_cse_v2.asymmetric]
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        // generate an asymmetric key pair for testing
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        // snippet-start:[s3.java.s3_cse_v2.auth]
        String s3ObjectKey = "EncryptedContent2.txt";
        String s3ObjectContent = "This is the 2nd content to encrypt";

        AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfigurationV2().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterialsProvider(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(keyPair)))
                .build();

        s3Encryption.putObject(bucketName, s3ObjectKey, s3ObjectContent);
        // snippet-end:[s3.java.s3_cse_v2.auth]
        System.out.println(s3Encryption.getObjectAsString(bucketName, s3ObjectKey));
        s3Encryption.shutdown();
        // snippet-end:[s3.java.s3_cse_v2.asymmetric]
    }

    public static void putEncryptedData3_Kms() {

        // snippet-start:[s3.java.s3_cse-v2.kms]
        AWSKMS kmsClient = AWSKMSClientBuilder.standard()
                .withRegion(Regions.DEFAULT_REGION)
                .build();

        // create CMK for for testing this example
        CreateKeyRequest createKeyRequest = new CreateKeyRequest();
        CreateKeyResult createKeyResult = kmsClient.createKey(createKeyRequest);

        // specify an Amazon KMS customer master key (CMK) ID
        String keyId = createKeyResult.getKeyMetadata().getKeyId();

        String s3ObjectKey = "EncryptedContent3.txt";
        String s3ObjectContent = "This is the 3rd content to encrypt";

        AmazonS3EncryptionV2 s3Encryption = AmazonS3EncryptionClientV2Builder.standard()
                .withRegion(Regions.US_WEST_2)
                .withCryptoConfiguration(new CryptoConfigurationV2().withCryptoMode(CryptoMode.StrictAuthenticatedEncryption))
                .withEncryptionMaterialsProvider(new KMSEncryptionMaterialsProvider(keyId))
                .build();

        s3Encryption.putObject(bucketName, s3ObjectKey, s3ObjectContent);
        System.out.println(s3Encryption.getObjectAsString(bucketName, s3ObjectKey));

        // schedule deletion of CMK generated for testing
        ScheduleKeyDeletionRequest scheduleKeyDeletionRequest =
                new ScheduleKeyDeletionRequest().withKeyId(keyId).withPendingWindowInDays(7);
        kmsClient.scheduleKeyDeletion(scheduleKeyDeletionRequest);

        s3Encryption.shutdown();
        kmsClient.shutdown();
       // snippet-end:[s3.java.s3_cse-v2.kms]
    }
}
