//snippet-sourcedescription:[ListGroups.java demonstrates how to get list of groups in AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.identitystore;

// snippet-start:[Identitystore.java2.list_groups.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.ListGroupsRequest;
import software.amazon.awssdk.services.identitystore.model.ListGroupsResponse;
import software.amazon.awssdk.services.identitystore.model.Group;
// snippet-end:[Identitystore.java2.list_groups.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListGroups {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n\n" ;


        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        int result = listGroups(identitystore, identitystoreId);
        System.out.println("Total number of groups is: " + result);
        identitystore.close();

    }

    // snippet-start:[identitystore.java2.list_groups.main]
    public static int listGroups(IdentitystoreClient identitystore, String identitystoreId) {
        try {

            boolean done = false;
            int count = 0;
            String nextToken = null;

            while(!done) {
                ListGroupsResponse response;
                if (nextToken == null){
                    ListGroupsRequest request = ListGroupsRequest.builder().identityStoreId(identitystoreId).build();
                    response = identitystore.listGroups(request);
                } else {
                    ListGroupsRequest request = ListGroupsRequest.builder().nextToken(nextToken).identityStoreId(identitystoreId).build();
                    response = identitystore.listGroups(request);
                }

                for(Group group : response.groups()) {
                    count ++;
                    System.out.format("GroupName: %s, GroupId: %s, GroupDescription: %s\n", group.displayName(), group.groupId(), group.description());
                }

                nextToken = response.nextToken();
                if (nextToken == null){
                    done = true;
                }
            }
            return count;

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return 0;
     }
     // snippet-end:[identitystore.java2.list_groups.main]
}
