// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.ListAccountAliasesResult;

/**
 * Lists all aliases associated with an AWS account
 */
public class ListAccountAliases {
    public static void main(String[] args) {

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        ListAccountAliasesResult response = iam.listAccountAliases();

        for (String alias : response.getAccountAliases()) {
            System.out.printf("Retrieved account alias %s", alias);
        }
    }
}
