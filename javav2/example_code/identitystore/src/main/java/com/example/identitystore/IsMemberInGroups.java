//snippet-sourcedescription:[IsMemberInGroups.java checks the user's membership in all requested groups and returns if the member exists in all queried groups in a AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.is_member_in_groups.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.IsMemberInGroupsRequest;
import software.amazon.awssdk.services.identitystore.model.IsMemberInGroupsResponse;
import software.amazon.awssdk.services.identitystore.model.MemberId;
import software.amazon.awssdk.services.identitystore.model.GroupMembershipExistenceResult;
import java.util.ArrayList;
import java.util.List;
// snippet-end:[Identitystore.java2.is_member_in_groups.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class IsMemberInGroups {

    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <userId> <list of groupIds> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userId - The id of the user. \n" +
        "    list of groupIds - The list of groupids of one or more groups. \n\n" ;

        if (args.length < 3) {
            System.out.println(usage);
            System.exit(1);
        }

        List<String> groupIdList = new ArrayList<>();
        String identitystoreId = args[0];
        String userId = args[1];

        for (int i=2; i < args.length; i++) {
            groupIdList.add(args[i]);
        }

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = isMemberInGroups(identitystore, identitystoreId, userId, groupIdList);
        System.out.println("Results: \n " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.is_member_in_groups.main]
    public static String isMemberInGroups(IdentitystoreClient identitystore, String identitystoreId, String userId, List<String> groupIdList) {
        try {
            
            MemberId memberId = MemberId.builder()
            .userId(userId)
            .build();

            IsMemberInGroupsRequest request = IsMemberInGroupsRequest.builder()
                              .identityStoreId(identitystoreId)
                              .memberId(memberId)
                              .groupIds(groupIdList)
                              .build();

            IsMemberInGroupsResponse response = identitystore.isMemberInGroups(request);
            System.out.format("Results: \n");
            for(GroupMembershipExistenceResult result: response.results()) {
                System.out.format("GroupId: %s, UserId: %s, MembershipExists: %s\n", result.groupId(), result.memberId().userId(), result.membershipExists());

            }
            return "Done";

        } catch (IdentitystoreException e) {  
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.is_member_in_groups.main]
}
