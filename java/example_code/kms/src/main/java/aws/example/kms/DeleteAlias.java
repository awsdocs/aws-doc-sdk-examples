// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package aws.example.kms;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DeleteAliasRequest;

public class DeleteAlias {
    public static void main(String[] args) {
        final String USAGE = "To run this example, supply an alias name\n" +
                "Usage: DeleteAlias <alias-name>\n" +
                "Example: DeleteAlias alias/projectKey1\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String aliasName = args[0];

        AWSKMS kmsClient = AWSKMSClientBuilder.standard().build();

        // Delete an alias for a CMK

        DeleteAliasRequest req = new DeleteAliasRequest().withAliasName(aliasName);
        kmsClient.deleteAlias(req);
    }
}
