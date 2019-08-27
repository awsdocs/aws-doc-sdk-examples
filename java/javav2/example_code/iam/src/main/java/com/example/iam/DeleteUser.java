//snippet-sourcedescription:[DeleteUser.java demonstrates how to delete an IAM user. This is only possible for users with no associated resources.]
//snippet-keyword:[SDK for Java 2.0]
//snippet-keyword:[Code Sample]
//snippet-service:[iam]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[soo-aws]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.example.iam;
// snippet-start:[iam.java2.delete_user.complete]
// snippet-start:[iam.java2.delete_user.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.DeleteConflictException;
import software.amazon.awssdk.services.iam.model.DeleteUserRequest;
 
// snippet-end:[iam.java2.delete_user.import]
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

        // snippet-start:[iam.java2.delete_user.main]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        DeleteUserRequest request = DeleteUserRequest.builder()
        		.userName(username).build();

        try {
            iam.deleteUser(request);
        } catch (DeleteConflictException e) {
            System.out.println("Unable to delete user. Verify user is not" +
                    " associated with any resources");
            throw e;
        }
        // snippet-end:[iam.java2.delete_user.main]

        System.out.println("Successfully deleted IAM user " + username);
    }
}
 
// snippet-end:[iam.java2.delete_user.complete]
