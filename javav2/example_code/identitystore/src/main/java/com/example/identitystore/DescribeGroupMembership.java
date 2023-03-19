//snippet-sourcedescription:[DescribeGroupMembership.java demonstrates how to retrieves membership metadata and attributes from membershipId in an AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.describe_group_membership.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.DescribeGroupMembershipRequest;
import software.amazon.awssdk.services.identitystore.model.DescribeGroupMembershipResponse;
// snippet-end:[Identitystore.java2.describe_group_membership.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeGroupMembership {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <membershipId>\n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    membershipId - The id of the member of a group. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String membershipId = args[1];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = describeGroupMembershipId(identitystore, identitystoreId, membershipId);
        System.out.println("GroupMembershipDescription: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.describe_group_membership.main]
    public static String describeGroupMembershipId(IdentitystoreClient identitystore, String identitystoreId, String membershipId) {
        try {

            DescribeGroupMembershipRequest request = DescribeGroupMembershipRequest.builder()
                              .identityStoreId(identitystoreId)
                              .membershipId(membershipId)
                              .build();

            DescribeGroupMembershipResponse response = identitystore.describeGroupMembership(request);

            return response.groupId() + " " + response.memberId();
        } catch (IdentitystoreException e) {  
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.describe_group_membership.main]
}
