// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DisableKeyRequest;

public class DisableCustomerMasterKey {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a key id or ARN\n" +
                "Usage: DisableCustomerMasterKey <key-id>\n" +
                "Example: DisableCustomerMasterKey 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Disable a CMK

        DisableKeyRequest req = new DisableKeyRequest().withKeyId(keyId);
        kmsClient.disableKey(req);

    }
}
