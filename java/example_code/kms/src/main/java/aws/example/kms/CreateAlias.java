// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.CreateAliasRequest;

public class CreateAlias {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply a key id or ARN and an alias name\n" +
                "Usage: CreateAlias <key-id> <alias-name>\n" +
                "Example: CreateAlias 1234abcd-12ab-34cd-56ef-1234567890ab " +
                "alias/projectKey1\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String targetKeyId = args[0];
        String aliasName = args[1];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Create an alias for a CMK

        CreateAliasRequest req = new CreateAliasRequest().withAliasName(aliasName).withTargetKeyId(targetKeyId);
        kmsClient.createAlias(req);
    }

}
