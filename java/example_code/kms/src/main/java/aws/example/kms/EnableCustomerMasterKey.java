// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.EnableKeyRequest;

public class EnableCustomerMasterKey {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a key id or ARN\n" +
                "Usage: EnableCustomerMasterKey <key-id>\n" +
                "Example: EnableCustomerMasterKey 1234abcd-12ab-34cd-56ef-1234567890ab\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String keyId = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Enable a CMK

        EnableKeyRequest req = new EnableKeyRequest().withKeyId(keyId);
        kmsClient.enableKey(req);

    }
}
