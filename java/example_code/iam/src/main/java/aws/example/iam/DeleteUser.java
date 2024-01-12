// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.DeleteConflictException;
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest;

/**
 * Deletes an IAM user. This is only possible for users with no associated
 * resources
 */
public class DeleteUser {
    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a username\n" +
                "Ex: DeleteUser <username>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        DeleteUserRequest request = new DeleteUserRequest()
                .withUserName(username);

        try {
            iam.deleteUser(request);
        } catch (DeleteConflictException e) {
            System.out.println("Unable to delete user. Verify user is not" +
                    " associated with any resources");
            throw e;
        }

        System.out.println("Successfully deleted IAM user " + username);
    }
}
