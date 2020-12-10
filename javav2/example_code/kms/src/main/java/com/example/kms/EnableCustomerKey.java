//snippet-sourcedescription:[EnableCustomerKey.java demonstrates how to enable an AWS Key Management Service (AWS KMS) key.]
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

// snippet-start:[kms.java2_enable_key.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.EnableKeyRequest;
// snippet-end:[kms.java2_enable_key.import]

public class EnableCustomerKey {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    EnableCustomerKey <keyId> \n\n" +
                "Where:\n" +
                "    keyId - a key id value to enable (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        enableKey(kmsClient, keyId);
        kmsClient.close();
    }

    // snippet-start:[kms.java2_enable_key.main]
    public static void enableKey(KmsClient kmsClient, String keyId) {

    try {
        EnableKeyRequest enableKeyRequest = EnableKeyRequest.builder()
                .keyId(keyId)
                .build();

        kmsClient.enableKey(enableKeyRequest);

    } catch (KmsException e) {
        System.err.println(e.getMessage());
        System.exit(1);
    }
        // snippet-end:[kms.java2_enable_key.main]
  }
}
