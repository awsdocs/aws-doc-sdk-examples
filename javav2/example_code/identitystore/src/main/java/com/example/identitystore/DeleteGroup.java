//snippet-sourcedescription:[DeleteGroup.java demonstrates how to delete a group within an AWS Identitystore given groupId.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.delete_group.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.DeleteGroupRequest;
import software.amazon.awssdk.services.identitystore.model.DeleteGroupResponse;
// snippet-end:[Identitystore.java2.delete_group.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteGroup {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupId>\n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupId - The id of the group to delete. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupID = args[1];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = deleteGroup(identitystore, identitystoreId, groupID);
        System.out.println("Successfully deleted the group: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.delete_group.main]
    public static String deleteGroup(IdentitystoreClient identitystore, String identitystoreId, String groupId) {
        try {

            DeleteGroupRequest request = DeleteGroupRequest.builder()
                              .identityStoreId(identitystoreId)
                              .groupId(groupId)
                              .build();

            DeleteGroupResponse response = identitystore.deleteGroup(request);

            return groupId;

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.delete_group.main]
}
