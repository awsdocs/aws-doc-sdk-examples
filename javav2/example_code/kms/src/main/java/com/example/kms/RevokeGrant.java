//snippet-sourcedescription:[RevokeGrant.java demonstrates how to revoke a grant for the specified customer master key (CMK).]
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

// snippet-start:[kms.java2_revoke_grant.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.RevokeGrantRequest;
// snippet-end:[kms.java2_revoke_grant.import]

public class RevokeGrant {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    RevokeGrant <keyId> <grantId> \n\n" +
                "Where:\n" +
                "    keyId - a unique identifier for the customer master key associated with the grant (for example, xxxxxbcd-12ab-34cd-56ef-1234567890ab). \n\n" +
                "    grantId - a grant id value of the grant revoke. \n\n" ;

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        String grantId = args[1];

        Region region = Region.US_WEST_2;
        KmsClient kmsClient = KmsClient.builder()
                .region(region)
                .build();

        revokeKeyGrant(kmsClient, keyId, grantId);
        kmsClient.close();
    }

    // snippet-start:[kms.java2_revoke_grant.main]
    public static void revokeKeyGrant(KmsClient kmsClient, String keyId, String grantId) {

        try {
            RevokeGrantRequest grantRequest = RevokeGrantRequest.builder()
                .keyId(keyId)
                .grantId(grantId)
                .build();

            kmsClient.revokeGrant(grantRequest);
        } catch (KmsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
     }
    // snippet-end:[kms.java2_revoke_grant.main]
    }
