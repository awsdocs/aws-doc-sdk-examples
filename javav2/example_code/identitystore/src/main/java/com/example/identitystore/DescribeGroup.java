// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.identitystore;

// snippet-start:[identitystore.java2.describe_group.main]
// snippet-start:[Identitystore.java2.describe_group.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.DescribeGroupRequest;
import software.amazon.awssdk.services.identitystore.model.DescribeGroupResponse;
// snippet-end:[Identitystore.java2.describe_group.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeGroup {
    public static void main(String... args) {

        final String usage = """

                Usage:
                    <identitystoreId> <groupId>

                Where:
                    identitystoreId - The id of the identitystore.\s
                    groupId - The id of the group.\s

                """;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupId = args[1];
        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();
        String result = describeGroup(identitystore, identitystoreId, groupId);
        System.out.println("GroupDisplayName GroupDescription: " + result);
        identitystore.close();
    }

    public static String describeGroup(IdentitystoreClient identitystore, String identitystoreId, String groupId) {
        try {

            DescribeGroupRequest request = DescribeGroupRequest.builder()
                    .identityStoreId(identitystoreId)
                    .groupId(groupId)
                    .build();

            DescribeGroupResponse response = identitystore.describeGroup(request);
            return response.displayName() + " " + response.description();

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
    }
}
// snippet-end:[identitystore.java2.describe_group.main]