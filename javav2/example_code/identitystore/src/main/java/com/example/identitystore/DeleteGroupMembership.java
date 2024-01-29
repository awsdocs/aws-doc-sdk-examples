// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.identitystore;

// snippet-start:[identitystore.java2.delete_group_membership.main]
// snippet-start:[Identitystore.java2.delete_group_membership.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.DeleteGroupMembershipRequest;
import software.amazon.awssdk.services.identitystore.model.DeleteGroupMembershipResponse;
// snippet-end:[Identitystore.java2.delete_group_membership.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteGroupMembership {
    public static void main(String... args) {
        final String usage = """

                Usage:
                    <identitystoreId> <membershipId>

                Where:
                    identitystoreId - The id of the identitystore.\s
                    membershipId - The id of the user member of the group.\s

                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String membershipId = args[1];
        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();
        String result = deleteGroupMembership(identitystore, identitystoreId, membershipId);
        System.out.println("Successfully removed the user from the group: " + result);
        identitystore.close();
    }

    public static String deleteGroupMembership(IdentitystoreClient identitystore, String identitystoreId,
            String membershipId) {
        try {
            DeleteGroupMembershipRequest request = DeleteGroupMembershipRequest.builder()
                    .identityStoreId(identitystoreId)
                    .membershipId(membershipId)
                    .build();

            DeleteGroupMembershipResponse response = identitystore.deleteGroupMembership(request);
            return membershipId;

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }
}
// snippet-end:[identitystore.java2.delete_group_membership.main]
