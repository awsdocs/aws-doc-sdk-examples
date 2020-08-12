//snippet-sourcedescription:[EncryptDataKey.java demonstrates how to encrypt and decrypt data using a key.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/10/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.*
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.kms;

// snippet-start:[kms.java2_encrypt_data.import]
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
// snippet-end:[kms.java2_encrypt_data.import]

public class EncryptDataKey {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a key id value\n" +
                        "Usage: EncryptDataKey <key-id>\n" +
                        "Example: EncryptDataKey 1234abcd-12ab-34cd-56ef-1234567890ab \n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        SdkBytes encryData = encryptData(kmsClient, keyId);
        decryptData(kmsClient, encryData, keyId);
        System.out.println("Done");
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

             // Get the encrypted data
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
            SdkBytes plainText = decryptResponse.plaintext();

    } catch (KmsException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
  }
    // snippet-end:[kms.java2_decrypt_data.main]
}
