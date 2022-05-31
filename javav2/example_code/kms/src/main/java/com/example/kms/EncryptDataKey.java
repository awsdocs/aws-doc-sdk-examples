//snippet-sourcedescription:[EncryptDataKey.java demonstrates how to encrypt and decrypt data by using an AWS Key Management Service (KMS) key.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kms;

// snippet-start:[kms.java2_encrypt_data.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
// snippet-end:[kms.java2_encrypt_data.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class EncryptDataKey {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <keyId> \n\n" +
                "Where:\n" +
                "    keyId - A key id value to use to encrypt/decrypt the data (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String keyId = args[0];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        SdkBytes encryData = encryptData(kmsClient, keyId);
        decryptData(kmsClient, encryData, keyId);
        System.out.println("Done");
        kmsClient.close();
    }

     // snippet-start:[kms.java2_encrypt_data.main]
     public static SdkBytes encryptData(KmsClient kmsClient, String keyId) {

         try {
             SdkBytes myBytes = SdkBytes.fromByteArray(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

             EncryptRequest encryptRequest = EncryptRequest.builder()
                     .keyId(keyId)
                     .plaintext(myBytes)
                     .build();

             EncryptResponse response = kmsClient.encrypt(encryptRequest);
             String algorithm = response.encryptionAlgorithm().toString();
             System.out.println("The encryption algorithm is " + algorithm);

             // Get the encrypted data.
             SdkBytes encryptedData = response.ciphertextBlob();
             return encryptedData;

         } catch (KmsException e) {
             System.err.println(e.getMessage());
             System.exit(1);
         }
         return null;
     }
    // snippet-end:[kms.java2_encrypt_data.main]

    // snippet-start:[kms.java2_decrypt_data.main]
    public static void decryptData(KmsClient kmsClient, SdkBytes encryptedData, String keyId) {

    try {
         DecryptRequest decryptRequest = DecryptRequest.builder()
                 .ciphertextBlob(encryptedData)
                 .keyId(keyId)
                 .build();

            DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
            decryptResponse.plaintext();

    } catch (KmsException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
  }
    // snippet-end:[kms.java2_decrypt_data.main]
}
