// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package aws.example.iam;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.ListUsersRequest;
import com.amazonaws.services.identitymanagement.model.ListUsersResult;
import com.amazonaws.services.identitymanagement.model.User;

/**
 * Lists all IAM users
 */
public class ListUsers {
    public static void main(String[] args) {

        final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.defaultClient();

        boolean done = false;
        ListUsersRequest request = new ListUsersRequest();

        while (!done) {
            ListUsersResult response = iam.listUsers(request);

            for (User user : response.getUsers()) {
                System.out.format("Retrieved user %s", user.getUserName());
            }

            request.setMarker(response.getMarker());

            if (!response.getIsTruncated()) {
                done = true;
            }
        }
    }
}
