//snippet-sourcedescription:[ListUsers.java demonstrates how to list Amazon Connect instance users.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon Connect]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.connect;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.connect.model.ConnectException;
import software.amazon.awssdk.services.connect.model.ListUsersRequest;
import software.amazon.awssdk.services.connect.model.ListUsersResponse;
import software.amazon.awssdk.services.connect.model.UserSummary;

import java.util.List;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListUsers {
    public static void main(String[] args) {
        final String usage = "\n" +
            "Usage: " +
            "   <instanceId>\n\n" +
            "Where:\n" +
            "   instanceId - The id of the Amazon Connect instance.\n\n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String instanceId = args[0];
        Region region = Region.US_EAST_1;
        ConnectClient connectClient = ConnectClient.builder()
            .region(region)
            .build();

        getUsers(connectClient, instanceId);
    }

    // snippet-start:[connect.java2.list.users.main]
    public static void getUsers( ConnectClient connectClient, String instanceId) {
        try {
            ListUsersRequest usersRequest = ListUsersRequest.builder()
                .instanceId(instanceId)
                .maxResults(10)
                .build();

            ListUsersResponse response = connectClient.listUsers(usersRequest);
            List<UserSummary> users = response.userSummaryList();
            for (UserSummary user: users) {
               System.out.println("The user name of the user is "+user.username());
               System.out.println("The user id is  "+user.id());
            }

        } catch (ConnectException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
    }
    // snippet-end:[connect.java2.list.users.main]
 }
