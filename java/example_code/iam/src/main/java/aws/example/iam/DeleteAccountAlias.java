// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.DeleteAccountAliasRequest;
import com.amazonaws.services.identitymanagement.model.DeleteAccountAliasResult;

/**
 * Deletes an alias from an AWS account
 */
public class DeleteAccountAlias {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an account alias\n" +
                "Ex: DeleteAccountAlias <account-alias>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alias = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        DeleteAccountAliasRequest request = new DeleteAccountAliasRequest()
                .withAccountAlias(alias);

        DeleteAccountAliasResult response = iam.deleteAccountAlias(request);

        System.out.println("Successfully deleted account alias " + alias);
    }
}
