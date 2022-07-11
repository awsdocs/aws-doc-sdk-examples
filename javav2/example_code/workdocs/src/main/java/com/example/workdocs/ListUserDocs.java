//snippet-sourcedescription:[ListUserDocs.java demonstrates how to list user docs for the current user.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon WorkDocs]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/19/2022]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[workdocs.java2.list_user_docs.complete]

package com.example.workdocs;

// snippet-start:[workdocs.java2.list_user_docs.import]
import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.workdocs.WorkDocsClient;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsRequest;
import software.amazon.awssdk.services.workdocs.model.DescribeFolderContentsResponse;
import software.amazon.awssdk.services.workdocs.model.DocumentMetadata;
import software.amazon.awssdk.services.workdocs.model.DocumentVersionMetadata;
import software.amazon.awssdk.services.workdocs.model.WorkDocsException;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersResponse;
import software.amazon.awssdk.services.workdocs.model.DescribeUsersRequest;
import software.amazon.awssdk.services.workdocs.model.User;
// snippet-end:[workdocs.java2.list_user_docs.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListUserDocs {

    public static void main(String[] args) throws Exception {
        // Based on WorkDocs dev guide code at http://docs.aws.amazon.com/workdocs/latest/developerguide/connect-workdocs-role.html

        final String usage = "\n" +
                "Usage:\n" +
                "    <organizationId> <userEmail>  \n\n" +
                "Where:\n" +
                "    organizationId - Your organization Id value. You can obtain this value from the AWS Management Console. \n"+
                "    userEmail - A user email. \n" ;

        if (args.length != 2) {
             System.out.println(usage);
             System.exit(1);
         }

        String orgId = args[0];
        String userEmail = args[1];
        Region region = Region.US_WEST_2;
        WorkDocsClient workDocs = WorkDocsClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listDocs(workDocs, orgId, userEmail );
        workDocs.close();
    }

    // snippet-start:[workdocs.java2.list_user_docs.main]
    public static void listDocs(WorkDocsClient workDocs, String orgId, String userEmail ) {

       try {
        String folderId = getUserFolder(workDocs, orgId, userEmail);
        DescribeFolderContentsRequest dfcRequest = DescribeFolderContentsRequest.builder()
                .folderId(folderId)
                .build();

        DescribeFolderContentsResponse result = workDocs.describeFolderContents(dfcRequest);
        List<DocumentMetadata> userDocs = new ArrayList<>();
        userDocs.addAll(result.documents());
        System.out.println("Docs for user " + userEmail + ":");
        System.out.println("");

        for (DocumentMetadata doc: userDocs) {
            DocumentVersionMetadata md = doc.latestVersionMetadata();
            System.out.println("Name:          " + md.name());
            System.out.println("Size (bytes):  " + md.size());
            System.out.println("Last modified: " + md.modifiedTimestamp());
            System.out.println("Doc ID:        " + doc.id());
            System.out.println("");
        }

       } catch(WorkDocsException e) {
           System.out.println(e.getLocalizedMessage());
           System.exit(1);
       }
    }

    private static String getUserFolder(WorkDocsClient workDocs, String orgId, String user) {

        List<User> wdUsers = new ArrayList<>();
        try {
        String marker = null;

        do {
            DescribeUsersResponse result;

            if(marker == null) {
                DescribeUsersRequest request = DescribeUsersRequest.builder()
                        .organizationId(orgId)
                        .query(user)
                        .build();
                result = workDocs.describeUsers(request);
            } else {
                DescribeUsersRequest request = DescribeUsersRequest.builder()
                        .organizationId(orgId)
                        .query(user)
                        .marker(marker)
                        .build();
                result = workDocs.describeUsers(request);
            }

            wdUsers.addAll(result.users());
            marker = result.marker();
        } while (marker != null);

        for (User wdUser : wdUsers) {
            return wdUser.rootFolderId();
        }

        } catch(WorkDocsException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(1);
        }
        return "";
    }
    // snippet-end:[workdocs.java2.list_user_docs.main]
}

// snippet-end:[workdocs.java2.list_user_docs.complete]