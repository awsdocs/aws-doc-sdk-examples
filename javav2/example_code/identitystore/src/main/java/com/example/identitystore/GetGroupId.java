//snippet-sourcedescription:[GetGroupId.java demonstrates how to retrieve the groupid based on other unique keys from AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.get_group_id.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.GetGroupIdRequest;
import software.amazon.awssdk.services.identitystore.model.GetGroupIdResponse;
import software.amazon.awssdk.services.identitystore.model.AlternateIdentifier;
import software.amazon.awssdk.services.identitystore.model.UniqueAttribute;
import software.amazon.awssdk.core.document.Document;
// snippet-end:[Identitystore.java2.get_group_id.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */


public class GetGroupId {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <groupAttributeName> <groupAttributeValue> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    groupAttributeName - The name of the unique attribute of the group. \n" +
        "    groupAttributeValue - The value of the specified group attribute. \n\n" ;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }
        String identitystoreId = args[0];
        String groupAttributeName = args[1];
        String groupAttributeValue = args[2];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = getGroupId(identitystore, identitystoreId, groupAttributeName, groupAttributeValue);
        System.out.println("GroupId: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.get_group_id.main]
    public static String getGroupId(IdentitystoreClient identitystore, String identitystoreId, String groupAttributeName, String groupAttributeValue) {
        try {

            String attributePath = groupAttributeName;
            Document attributeValue = Document.fromString(groupAttributeValue);

            UniqueAttribute uniqueAttribute = UniqueAttribute.builder()
                 .attributePath(attributePath)
                 .attributeValue(attributeValue)
                 .build();

            AlternateIdentifier alternateIdentifier = AlternateIdentifier.builder()
                    .uniqueAttribute(uniqueAttribute)
                    .build();

            GetGroupIdRequest request = GetGroupIdRequest.builder()
                              .identityStoreId(identitystoreId)
                              .alternateIdentifier(alternateIdentifier)
                              .build();

            GetGroupIdResponse response = identitystore.getGroupId(request);
            return response.groupId();

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.get_group_id.main]
}