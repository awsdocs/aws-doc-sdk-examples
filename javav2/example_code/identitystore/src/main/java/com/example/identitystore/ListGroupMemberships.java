//snippet-sourcedescription:[ListGroupMemberships.java demonstrates how to retrieve the list of users attached to a group in AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.list_group_memberships.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.ListGroupMembershipsRequest;
import software.amazon.awssdk.services.identitystore.model.ListGroupMembershipsResponse;
import software.amazon.awssdk.services.identitystore.model.GroupMembership;
// snippet-end:[Identitystore.java2.list_group_memberships.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */


public class ListGroupMemberships {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupId - The id of the group. \n\n" ;


        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupId = args[1];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        int result = listGroupMemberships(identitystore, identitystoreId, groupId);
        System.out.println("Total number of memberships in a group is: " + result);
        identitystore.close();

    }

    // snippet-start:[identitystore.java2.list_group_memberships.main]
    public static int listGroupMemberships(IdentitystoreClient identitystore, String identitystoreId, String groupId) {
        try {
            boolean done = false;
            int count = 0;
            String nextToken = null;

            while(!done) {
                ListGroupMembershipsResponse response;
                if (nextToken == null){
                    ListGroupMembershipsRequest request = ListGroupMembershipsRequest.builder()
                        .identityStoreId(identitystoreId)
                        .groupId(groupId)
                        .build();
                    response = identitystore.listGroupMemberships(request);
                } else {
                    ListGroupMembershipsRequest request = ListGroupMembershipsRequest.builder()
                    .nextToken(nextToken)
                    .identityStoreId(identitystoreId)
                    .groupId(groupId)
                    .build();
                    response = identitystore.listGroupMemberships(request);
                }

                for(GroupMembership groupmembership : response.groupMemberships()) {
                    count ++;
                    System.out.format("GroupId: %s, UserId: %s, MembershipId: %s\n", groupmembership.groupId(), groupmembership.memberId().userId(), groupmembership.membershipId());
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
     // snippet-end:[identitystore.java2.list_group_memberships.main]
}
