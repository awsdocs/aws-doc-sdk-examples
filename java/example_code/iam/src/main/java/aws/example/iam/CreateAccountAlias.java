// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateAccountAliasRequest;
import com.amazonaws.services.identitymanagement.model.CreateAccountAliasResult;

/**
 * Creates an alias for an AWS Account
 */
public class CreateAccountAlias {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply an alias\n" +
                "Ex: CreateAccountAlias <alias>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String alias = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        CreateAccountAliasRequest request = new CreateAccountAliasRequest()
                .withAccountAlias(alias);

        CreateAccountAliasResult response = iam.createAccountAlias(request);

        System.out.println("Successfully created account alias: " + alias);
    }
}
