//snippet-sourcedescription:[DisableCustomerKey.java demonstrates how to disable an AWS Key Management Service (AWS KMS) key.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Key Management Service]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/02/2020]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.kms;

// snippet-start:[kms.java2_disable_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DisableKeyRequest;
import software.amazon.awssdk.services.kms.model.KmsException;
// snippet-end:[kms.java2_disable_key.import]

public class DisableCustomerKey {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DisableCustomerKey <keyId> \n\n" +
                "Where:\n" +
                "    keyId - a key id value to disable (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        disableKey(kmsClient, keyId);
        kmsClient.close();
    }

    // snippet-start:[kms.java2_disable_key.main]
    public static void disableKey( KmsClient kmsClient, String keyId) {

       try {

           DisableKeyRequest keyRequest = DisableKeyRequest.builder()
                .keyId(keyId)
                .build();

           kmsClient.disableKey(keyRequest);
        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        // snippet-end:[kms.java2_disable_key.main]
    }
}
