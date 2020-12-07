//snippet-sourcedescription:[ListUsers.java demonstrates how to list the users for an organization.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon WorkDocs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/06/2020]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[workdocs.java2.list_users.complete]

package com.example.workdocs;
// snippet-start:[workdocs.java2.list_users.import]
import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.User;
// snippet-end:[workdocs.java2.list_users.import]

public class ListUsers {

    public static void main(String[] args) {
        // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-iam.html

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListUsers <organizationId>   \n\n" +
                "Where:\n" +
                "    organizationId - your organization Id value. You can obtain this value from the AWS Management Console. \n" ;

       if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String orgId = args[0];
        Region region = Region.US_WEST_2;
        WorkDocsClient workDocs = WorkDocsClient.builder()
                .region(region)
                .build();

        getAllUsers(workDocs, orgId);
        workDocs.close();
    }

    // snippet-start:[workdocs.java2.list_users.main]
    public static void getAllUsers(WorkDocsClient workDocs,String orgId) {

        List<User> wdUsers = new ArrayList<>();

        String marker = null;

        do {
            DescribeUsersResponse result;

            if(marker == null) {
                DescribeUsersRequest request = DescribeUsersRequest.builder()
                        .organizationId(orgId)
                        .build();
                result = workDocs.describeUsers(request);
            } else {
                DescribeUsersRequest request = DescribeUsersRequest.builder()
                        .organizationId(orgId)
                        .marker(marker)
                        .build();
                result = workDocs.describeUsers(request);
            }

            System.out.println("List of users:");

            wdUsers.addAll(result.users());
            marker = result.marker();
        } while (marker != null);

        for (User wdUser : wdUsers) {
            System.out.printf("Firstname:%s | Lastname:%s | Email:%s | root-folder-id:%s\n",
                    wdUser.givenName(), wdUser.surname(), wdUser.emailAddress(),
                    wdUser.rootFolderId());
        }
    }
}
// snippet-end:[workdocs.java2.list_users.main]
// snippet-end:[workdocs.java2.list_users.complete]