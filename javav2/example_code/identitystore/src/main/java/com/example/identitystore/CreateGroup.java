//snippet-sourcedescription:[CreateGroup.java demonstrates how to create a group within the specified AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.create_group.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.CreateGroupRequest;
import software.amazon.awssdk.services.identitystore.model.CreateGroupResponse;
// snippet-end:[Identitystore.java2.create_group.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateGroup {

    public static void main(String... args) {
        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupName> <groupDescription> \n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupName - The name of the group to create. \n" +
        "    groupDescription - The description of the group to create. \n\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupName = args[1];
        String groupDescription = args[2];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = createGroup(identitystore, identitystoreId, groupName, groupDescription);
        System.out.println("Successfully created group: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.create_group.main]
    public static String createGroup(IdentitystoreClient identitystore, String identitystoreId, String groupName, String groupDescription) {
        try {
            
            CreateGroupRequest request = CreateGroupRequest.builder()
                              .identityStoreId(identitystoreId)
                              .displayName(groupName)
                              .description(groupDescription)
                              .build();

            CreateGroupResponse response = identitystore.createGroup(request);
            return response.groupId();
        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";

     }
    // snippet-end:[identitystore.java2.create_group.main]
}
