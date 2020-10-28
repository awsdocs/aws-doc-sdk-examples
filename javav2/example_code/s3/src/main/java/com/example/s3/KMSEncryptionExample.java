//snippet-sourcedescription:[KMSEncryptionExample.java is a multi-service Java V2 example that demonstrates how to use the AWS KSM service to encrypt data prior to placing the data into an Amazon S3 bucket.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[10/28/2020]
//snippet-sourceauthor:[scmacdon-aws]

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

package com.example.s3;

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
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
// snippet-end:[s3.java2.kms.import]

/**
 * Before running this code example, you need to create a key by using the AWS Key Management Service. 
 * For information, see "Creating keys" in the AWS Key Management Service Developer Guide.
 */
public class KMSEncryptionExample {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    KMSEncryptionExample <objectname><bucketName> <objectPath><outPath><keyId>\n\n" +
                "Where:\n" +
                "    objectname - the name of the object \n\n" +
                "    bucketName - the bucket name that contains the object (i.e., bucket1)\n" +
                "    objectPath - the path to a TXT file to encrypt and place into a S3 bucket (i.e., C:\\AWS\\test.txt)\n" +
                "    outPath - the path where a text file is written to after it's decrypted (i.e., C:\\AWS\\testPlain.txt)\n" +
                "    keyId - the id of the KMS key to use to encrpt/decrypt the data. You can obtain the key ID value from the AWS KMS console\n";

         if (args.length < 5) {
             System.out.println(USAGE);
             System.exit(1);
        }

        /* Read the name from command args*/
        String objectKey = args[0];
        String bucketName = args[1];
        String objectPath = args[2];
        String outPath = args[3];
        String keyId = args[4];

        //Create the S3Client object
        Region region = Region.US_WEST_2;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        // Encrypt data and place the encrypted data into an Amazon S3 bucket
        putEncryptData(s3, objectKey, bucketName,  objectPath, keyId);

        // Get the encrypted data, decrypt it and write out the data to a text file
        getEncryptedData (s3, bucketName, objectKey, outPath, keyId );
    }

    // snippet-start:[s3.java2.kms.main]
    public static void putEncryptData(S3Client s3,
                                      String objectKey,
                                      String bucketName,
                                      String objectPath,
                                      String keyId) {

        try {

            // Put encrypted data into a S3 bucket
           PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();

           byte[] myData = getObjectFile(objectPath);

           // Encrypt the data by using the AWS Key Management Service
           byte[] encryptData = encryptData(keyId, myData);
           s3.putObject(objectRequest, RequestBody.fromBytes(encryptData));

        } catch (S3Exception | FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static byte[] encryptData(String keyId, byte[] data) {

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

            // Get the encrypted data
            SdkBytes encryptedData = response.ciphertextBlob();
            return encryptedData.asByteArray();
        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }


    private static void getEncryptedData(S3Client s3,
                                         String bucketName,
                                         String keyName,
                                         String path,
                                         String keyId) {

        try {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .key(keyName)
                    .bucket(bucketName)
                    .build();

            // Get the byte[] from the Amazon S3 bucket
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            byte[] data = objectBytes.asByteArray();

            // Decrypt the data by using the AWS Key Management Service
            byte[] unEncryptedData = decryptData(data, keyId);

            // Write the data to a local file
            File myFile = new File(path );
            OutputStream os = new FileOutputStream(myFile);
            os.write(unEncryptedData);
            System.out.println("Successfully obtained and decrypted bytes from the Amazon S3 bucket");

            // Close the file
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

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

    private static byte[] getObjectFile(String path) throws FileNotFoundException {
        byte[] bFile = readBytesFromFile(path);
        return bFile;
    }

    private static byte[] readBytesFromFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
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

    // Return KMS client
    private static KmsClient getKMSClient() {

        Region region = Region.US_EAST_1;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();
        return kmsClient;
    }
    // snippet-end:[s3.java2.kms.main]
}
