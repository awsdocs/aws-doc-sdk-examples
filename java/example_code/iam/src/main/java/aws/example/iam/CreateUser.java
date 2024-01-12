// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.CreateUserRequest;
import com.amazonaws.services.identitymanagement.model.CreateUserResult;

/**
 * Creates an IAM user
 */
public class CreateUser {

    public static void main(String[] args) {

        final String USAGE = "To run this example, supply a username\n" +
                "Ex: CreateUser <username>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        CreateUserRequest request = new CreateUserRequest()
                .withUserName(username);

        CreateUserResult response = iam.createUser(request);

        System.out.println("Successfully created user: " +
                response.getUser().getUserName());
    }
}
