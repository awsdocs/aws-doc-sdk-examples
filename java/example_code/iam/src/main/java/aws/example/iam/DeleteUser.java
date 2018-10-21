 
//snippet-sourcedescription:[DeleteUser.java demonstrates how to ...]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
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

        final String USAGE =
            "To run this example, supply a username\n" +
            "Ex: DeleteUser <username>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String username = args[0];

        final AmazonIdentityManagement iam =
            AmazonIdentityManagementClientBuilder.defaultClient();

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

