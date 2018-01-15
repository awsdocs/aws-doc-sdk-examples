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
package com.example.iam;
import software.amazon.awssdk.core.regions.Region;
import software.amazon.awssdk.services.iam.IAMClient;
import software.amazon.awssdk.services.iam.model.UpdateUserRequest;
import software.amazon.awssdk.services.iam.model.UpdateUserResponse;

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

        Region region = Region.AWS_GLOBAL;
        IAMClient iam = IAMClient.builder().region(region).build();

        UpdateUserRequest request = UpdateUserRequest.builder()
            .userName(cur_name)
            .newUserName(new_name).build();

        UpdateUserResponse response = iam.updateUser(request);

        System.out.printf("Successfully updated user to username %s",
                new_name);
    }
}

