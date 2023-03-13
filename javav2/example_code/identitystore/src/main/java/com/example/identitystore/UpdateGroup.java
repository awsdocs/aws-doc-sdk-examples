//snippet-sourcedescription:[UpdateGroup.java demonstrates how to update the group metadata and attributes in AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.update_group.import]
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.UpdateGroupRequest;
import software.amazon.awssdk.services.identitystore.model.UpdateGroupResponse;
import software.amazon.awssdk.services.identitystore.model.AttributeOperation;
// snippet-end:[Identitystore.java2.update_group.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UpdateGroup {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupId> <groupAttributeName> <groupAttributeValue> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupId - The id of the group. \n" +
        "    groupAttributeName - The name of the unique attribute of the group. \n" +
        "    groupAttributeValue - The value of the specified group attribute. \n\n" ;


        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String groupId = args[1];
        String groupAttributePath = args[2];
        String groupAttributeValue = args[3];


        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = updateGroup(identitystore, identitystoreId, groupId, groupAttributePath, groupAttributeValue);
        System.out.println("Group:" + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.update_group.main]
    public static String updateGroup(IdentitystoreClient identitystore, String identitystoreId, String groupId, String groupAttributePath, String groupAttributeValue) {
        try {
            String attributePath = groupAttributePath;
            Document attributeValue = Document.fromString(groupAttributeValue);

            AttributeOperation attributeOperation = AttributeOperation.builder()
                 .attributePath(attributePath)
                 .attributeValue(attributeValue)
                 .build();

            UpdateGroupRequest request = UpdateGroupRequest.builder()
                              .identityStoreId(identitystoreId)
                              .groupId(groupId)
                              .operations(attributeOperation)
                              .build();

            UpdateGroupResponse response = identitystore.updateGroup(request);

            System.out.format("Group Field Name is updated with new value %s for groupId %s\n", attributePath, attributeValue, groupId);
            return "Updated";

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
    // snippet-end:[identitystore.java2.update_group.main]
}
