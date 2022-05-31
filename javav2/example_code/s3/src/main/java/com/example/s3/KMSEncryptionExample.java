//snippet-sourcedescription:[KMSEncryptionExample.java demonstrates how to use the AWS Key Management Service (AWS KMS) service to encrypt data prior to placing the data into an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/16/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.s3;

// snippet-start:[s3.java2.kms.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
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
 * Before running this code example, you need to create a key by using the AWS Key Management Service.
 * For information, see "Creating keys" in the AWS Key Management Service Developer Guide.
 *
 * In addition, before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 *  https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 */

public class KMSEncryptionExample {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <objectName> <bucketName> <objectPath> <outPath> <keyId>\n\n" +
                "Where:\n" +
                "    objectName - The name of the object. \n\n" +
                "    bucketName - The Amazon S3 bucket name that contains the object (for example, bucket1). \n" +
                "    objectPath - The path to a TXT file to encrypt and place into a Amazon S3 bucket (for example, C:/AWS/test.txt).\n" +
                "    outPath - The path where a text file is written to after it's decrypted (for example, C:/AWS/testPlain.txt).\n" +
                "    keyId - The id of the AWS KMS key to use to encrpt/decrypt the data. You can obtain the key ID value from the AWS Management Console.\n";

         if (args.length != 5) {
             System.out.println(usage);
             System.exit(1);
        }

        String objectName = args[0];
        String bucketName = args[1];
        String objectPath = args[2];
        String outPath = args[3];
        String keyId = args[4];

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.US_EAST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

         putEncryptData(s3, objectName, bucketName, objectPath, keyId);
         getEncryptedData (s3, bucketName, objectName, outPath, keyId );
         s3.close();
    }

    // snippet-start:[s3.java2.kms.main]
    // Encrypt data and place the encrypted data into an Amazon S3 bucket.
    public static void putEncryptData(S3Client s3,
                                      String objectName,
                                      String bucketName,
                                      String objectPath,
                                      String keyId) {

        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .build();

           byte[] myData = getObjectFile(objectPath);

           // Encrypt the data by using the AWS Key Management Service.
           byte[] encryptData = encryptData(keyId, myData);
           s3.putObject(objectRequest, RequestBody.fromBytes(encryptData));

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    // Obtain the encrypted data, decrypt it, and write the data to a text file
    public static void getEncryptedData(S3Client s3,
                                         String bucketName,
                                         String objectName,
                                         String path,
                                         String keyId) {

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

            // Write the data to a local file
            File myFile = new File(path );
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

    // Encrypt the data passed as a byte array.
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

    // Decrypt the data passed as a byte array.
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

    // Return a byte array.
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

    // Return a KmsClient object
    private static KmsClient getKMSClient() {

        Region region = Region.US_EAST_1;
        return KmsClient.builder()
                .region(region)
                .build();
    }
    // snippet-end:[s3.java2.kms.main]
}
