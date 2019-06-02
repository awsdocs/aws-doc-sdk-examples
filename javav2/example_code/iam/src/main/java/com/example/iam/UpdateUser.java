//snippet-sourcedescription:[UpdateUser.java demonstrates how to update the name of an IAM user.]
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
// snippet-start:[iam.java2.update_user.complete]
// snippet-start:[iam.java2.update_user.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.UpdateUserRequest;
import software.amazon.awssdk.services.iam.model.UpdateUserResponse;
 
// snippet-end:[iam.java2.update_user.import]
/**
 * Updates an IAM user's username
 */
public class UpdateUser {
    public static void main(String[] args) {

        final String USAGE =
            "To run this example, supply the current username and a new\n" +
            "username. Ex:\n\n" +
            "UpdateUser <current-name> <new-name>\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String cur_name = args[0];
        String new_name = args[1];

        // snippet-start:[iam.java2.update_user.main]
        Region region = Region.AWS_GLOBAL;
        IamClient iam = IamClient.builder().region(region).build();

        UpdateUserRequest request = UpdateUserRequest.builder()
            .userName(cur_name)
            .newUserName(new_name).build();

        UpdateUserResponse response = iam.updateUser(request);
        // snippet-end:[iam.java2.update_user.main]

        System.out.printf("Successfully updated user to username %s",
                new_name);
    }
}
 
// snippet-end:[iam.java2.update_user.complete]
