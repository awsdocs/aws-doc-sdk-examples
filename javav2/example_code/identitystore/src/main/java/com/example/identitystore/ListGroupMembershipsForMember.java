//snippet-sourcedescription:[ListGroupMembershipsForMember.java demonstrates how to get the list of groups that a user is attached to in AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.list_group_memberships_For_Member.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.ListGroupMembershipsForMemberRequest;
import software.amazon.awssdk.services.identitystore.model.ListGroupMembershipsForMemberResponse;
import software.amazon.awssdk.services.identitystore.model.MemberId;
import software.amazon.awssdk.services.identitystore.model.GroupMembership;
// snippet-end:[Identitystore.java2.list_group_memberships_For_Member.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListGroupMembershipsForMember {
    public static void main(String... args) {
        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <userId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userId - The id of the user. \n\n" ;


        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String userId = args[1];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        int result = listGroupMembershipsForMember(identitystore, identitystoreId, userId);
        System.out.println("Total number of groupmemberships for user is: %d\n:" + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.list_group_memberships_For_Member.main]
    public static int listGroupMembershipsForMember(IdentitystoreClient identitystore, String identitystoreId, String userId) {
        try {

            boolean done = false;
            int count = 0;
            String nextToken = null;

            MemberId memberId = MemberId.builder()
            .userId(userId)
            .build();

            while(!done) {
                ListGroupMembershipsForMemberResponse response;
                if (nextToken == null){
                    ListGroupMembershipsForMemberRequest request = ListGroupMembershipsForMemberRequest.builder()
                        .identityStoreId(identitystoreId)
                        .memberId(memberId)
                        .build();
                    response = identitystore.listGroupMembershipsForMember(request);
                } else {
                    ListGroupMembershipsForMemberRequest request = ListGroupMembershipsForMemberRequest.builder()
                    .nextToken(nextToken)
                    .identityStoreId(identitystoreId)
                    .memberId(memberId)
                    .build();
                    response = identitystore.listGroupMembershipsForMember(request);
                }

                for(GroupMembership groupMemberShip: response.groupMemberships()) {
                    count ++;
                    System.out.format("GroupId: %s, UserId: %s, MembershipId: %s\n", groupMemberShip.groupId(), groupMemberShip.memberId().userId(), groupMemberShip.membershipId());
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
     // snippet-end:[identitystore.java2.list_group_memberships_For_Member.main]
}
