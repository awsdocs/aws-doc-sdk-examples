// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.RevokeGrantRequest;

public class RevokeGrant {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a key id or ARN and a grant id\n" +
                "Usage: RevokeGrant <key-id> <grant-id>\n" +
                "Example: RevokeGrant 1234abcd-12ab-34cd-56ef-1234567890ab grant1\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];
        String grantId = args[1];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Revoke a grant on a CMK

        RevokeGrantRequest request = new RevokeGrantRequest().withKeyId(keyId).withGrantId(grantId);
        kmsClient.revokeGrant(request);
    }
}
