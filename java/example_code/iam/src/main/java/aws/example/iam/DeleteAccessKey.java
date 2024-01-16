// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyResult;

/**
 * Deletes an access key from an IAM user
 */
public class DeleteAccessKey {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a username and access key id\n" +
                "Ex: DeleteAccessKey <username> <access-key-id>\n";

        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];
        String access_key = args[1];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        DeleteAccessKeyRequest request = new DeleteAccessKeyRequest()
                .withAccessKeyId(access_key)
                .withUserName(username);

        DeleteAccessKeyResult response = iam.deleteAccessKey(request);

        System.out.println("Successfully deleted access key " + access_key +
                " from user " + username);
    }
}
