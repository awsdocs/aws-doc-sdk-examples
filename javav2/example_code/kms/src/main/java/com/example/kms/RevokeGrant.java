//snippet-sourcedescription:[RevokeGrant.java demonstrates how to revoke a grant.]
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

// snippet-start:[kms.java2_revoke_grant.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.KmsException;
import software.amazon.awssdk.services.kms.model.RevokeGrantRequest;
// snippet-end:[kms.java2_revoke_grant.import]

public class RevokeGrant {

    public static void main(String[] args) {

        final String USAGE =
                "To run this example, supply a key id and a grant id \n" +
                        "Usage: CreateGrant <key-id> <grantee-id>\n" +
                        "Example: RevokeGrant 1234abcd-12ab-34cd-56ef-1234567890ab " +
                        "000998808f25a981d32a271f03d440dcdd3b7dda6257ba26c4864cb337b5adb0\n";

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
