// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

// snippet-start:[s3.java2.kms.main]
// snippet-start:[s3.java2.kms.import]

import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
// snippet-end:[s3.java2.kms.import]

/**
 * Before running this code example, you need to create a key by using the AWS
 * Key Management Service.
 * For information, see "Creating keys" in the AWS Key Management Service
 * Developer Guide.
 * <p>
 * In addition, before running this Java V2 code example, set up your
 * development environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class KMSEncryptionExample {

    public static void main(String[] args) {
        final String usage = """

            Usage:
                <objectName> <bucketName> <objectPath> <outPath> <keyId>

            Where:
                objectName - The name of the object.\s
                bucketName - The Amazon S3 bucket name that contains the object (for example, bucket1).\s
                objectPath - The path to a TXT file to encrypt and place into a Amazon S3 bucket (for example, C:/AWS/test.txt).
                outPath - The path where a text file is written to after it's decrypted (for example, C:/AWS/testPlain.txt).
                keyId - The id of the AWS KMS key to use to encrpt/decrypt the data. You can obtain the key ID value from the AWS Management Console.
            """;

        if (args.length != 5) {
            System.out.println(usage);
            System.exit(1);
        }

        String objectName = args[0];
        String bucketName = args[1];
        String objectPath = args[2];
        String outPath = args[3];
        String keyId = args[4];
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
            .region(region)
            .build();

        putEncryptData(s3, objectName, bucketName, objectPath, keyId);
        getEncryptedData(s3, bucketName, objectName, outPath, keyId);
        s3.close();
    }

    /**
     * Uploads an encrypted object to an Amazon S3 bucket.
     *
     * @param s3 The {@link S3Client} instance used to interact with Amazon S3.
     * @param objectName The name of the object to be uploaded.
     * @param bucketName The name of the Amazon S3 bucket where the object will be uploaded.
     * @param objectPath The local file path of the object to be uploaded.
     * @param keyId The ID of the encryption key to be used for encrypting the object data.
     *
     * @throws S3Exception If an error occurs while uploading the object to Amazon S3.
     */
    public static void putEncryptData(S3Client s3, String objectName, String bucketName, String objectPath,
                                      String keyId) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

            byte[] myData = getObjectFile(objectPath);
            byte[] encryptData = encryptData(keyId, myData);
            s3.putObject(objectRequest, RequestBody.fromBytes(encryptData));

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Retrieves encrypted data from an Amazon S3 bucket, decrypts it using the specified AWS Key Management Service (KMS) key, and writes the decrypted data to a local file.
     *
     * @param s3 the S3Client instance used to interact with Amazon S3
     * @param bucketName the name of the Amazon S3 bucket to retrieve the encrypted data from
     * @param objectName the name of the object within the Amazon S3 bucket to retrieve
     * @param path the local file path where the decrypted data will be written
     * @param keyId the ID of the AWS KMS key to use for decrypting the data
     */
    public static void getEncryptedData(S3Client s3, String bucketName, String objectName, String path, String keyId) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                .key(objectName)
                .bucket(bucketName)
                .build();

            // Get the byte[] from the Amazon S3 bucket.
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            // Decrypt the data by using the AWS Key Management Service.
            byte[] unEncryptedData = decryptData(data, keyId);

            // Write the data to a local file.
            File myFile = new File(path);
            OutputStream os = new FileOutputStream(myFile);
            os.write(unEncryptedData);
            System.out.println("Successfully obtained and decrypted bytes from the Amazon S3 bucket");
            os.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    /**
     * Encrypts the given data using the specified key ID in the AWS Key Management Service (KMS).
     *
     * @param keyId the ID of the KMS key to use for encryption
     * @param data the data to be encrypted
     * @return the encrypted data as a byte array, or null if an error occurred during the encryption process
     */
    private static byte[] encryptData(String keyId, byte[] data) {
        try {
            KmsClient kmsClient = getKMSClient();
            SdkBytes myBytes = SdkBytes.fromByteArray(data);

            EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(myBytes)
                .build();

            EncryptResponse response = kmsClient.encrypt(encryptRequest);
            String algorithm = response.encryptionAlgorithm().toString();
            System.out.println("The encryption algorithm is " + algorithm);

            // Return the encrypted data.
            SdkBytes encryptedData = response.ciphertextBlob();
            return encryptedData.asByteArray();

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return null;
    }

    /**
     * Decrypts the given encrypted data using the specified key ID.
     *
     * @param data   the encrypted data to be decrypted
     * @param keyId  the ID of the KMS key to be used for decryption
     * @return the decrypted data as a byte array, or {@code null} if an error occurs
     */
    private static byte[] decryptData(byte[] data, String keyId) {

        try {
            KmsClient kmsClient = getKMSClient();
            SdkBytes encryptedData = SdkBytes.fromByteArray(data);

            DecryptRequest decryptRequest = DecryptRequest.builder()
                .ciphertextBlob(encryptedData)
                .keyId(keyId)
                .build();

            DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
            SdkBytes plainText = decryptResponse.plaintext();
            return plainText.asByteArray();

        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return null;
    }

    /**
     * Reads the contents of a file and returns it as a byte array.
     *
     * @param filePath the path to the file to be read
     * @return a byte array containing the contents of the file, or null if an error occurred
     */
    private static byte[] getObjectFile(String filePath) {
        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;
        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesArray;
    }

    // Return a KmsClient object.
    private static KmsClient getKMSClient() {

        Region region = Region.US_EAST_1;
        return KmsClient.builder()
            .region(region)
            .build();
    }
}
// snippet-end:[s3.java2.kms.main]
