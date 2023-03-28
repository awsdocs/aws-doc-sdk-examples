//snippet-sourcedescription:[GetGroupMembershipId.java demonstrates how to retrieve the user membership from a group in AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.get_group_membershipid.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.GetGroupMembershipIdRequest;
import software.amazon.awssdk.services.identitystore.model.GetGroupMembershipIdResponse;
import software.amazon.awssdk.services.identitystore.model.MemberId;
// snippet-end:[Identitystore.java2.get_group_membershipid.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetGroupMembershipId {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupId> <userId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupId - The id of the group. \n" +
        "    userId - The id of the user. \n\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupId = args[1];
        String userId = args[2];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = getGroupMembershipId(identitystore, identitystoreId, groupId, userId);
        System.out.println("MembershipId: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.get_group_membershipid.main]
    public static String getGroupMembershipId(IdentitystoreClient identitystore, String identitystoreId, String groupId, String userId) {
        try {
            
            MemberId memberId = MemberId.builder()
            .userId(userId)
            .build();

            GetGroupMembershipIdRequest request = GetGroupMembershipIdRequest.builder()
                              .identityStoreId(identitystoreId)
                              .groupId(groupId)
                              .memberId(memberId)
                              .build();

            GetGroupMembershipIdResponse response = identitystore.getGroupMembershipId(request);

            return response.membershipId();

        } catch (IdentitystoreException e) {  
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
    // snippet-end:[identitystore.java2.get_group_membershipid.main]
}
