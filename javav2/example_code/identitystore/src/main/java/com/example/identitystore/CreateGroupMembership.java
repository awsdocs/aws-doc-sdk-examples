//snippet-sourcedescription:[CreateGroupMembership.java demonstrates how to create a relationship between a member(user) and a group in a specified AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.create_group_membership.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.CreateGroupMembershipRequest;
import software.amazon.awssdk.services.identitystore.model.CreateGroupMembershipResponse;
import software.amazon.awssdk.services.identitystore.model.MemberId;
// snippet-end:[Identitystore.java2.create_group_membership.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateGroupMembership {

    public static void main(String... args) {
        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupId> <userId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupId - The id of the group. \n" +
        "    userId  - The id of the user. \n\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupID = args[1];
        String userID = args[2];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = createGroupMembership(identitystore, identitystoreId, groupID, userID);
        System.out.println("Successfully added the user to the group: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.create_group_membership.main]
    public static String createGroupMembership(IdentitystoreClient identitystore, String identitystoreId, String groupId, String userId) {
        try {

            MemberId memberId = MemberId.builder()
                    .userId(userId)
                    .build();

            CreateGroupMembershipRequest request = CreateGroupMembershipRequest.builder()
                              .identityStoreId(identitystoreId)
                              .groupId(groupId)
                              .memberId(memberId)
                              .build();

            CreateGroupMembershipResponse response = identitystore.createGroupMembership(request);

            return response.membershipId();
        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.create_group_membership.main]
}
