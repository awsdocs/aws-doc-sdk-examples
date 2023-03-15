//snippet-sourcedescription:[UpdateUser.java demonstrates how to update the user metadata and attributes in AWS Identitystore]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.update_user.import]
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.UpdateUserRequest;
import software.amazon.awssdk.services.identitystore.model.UpdateUserResponse;
import software.amazon.awssdk.services.identitystore.model.AttributeOperation;
// snippet-end:[Identitystore.java2.update_user.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class UpdateUser {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <userId> <userAttributeName> <userAttributeValue> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userId - The id of the user. \n" +
        "    userAttributeName - The name of the unique attribute of the user. \n" +
        "    userAttributeValue - The value of the specified user attribute. \n\n" ;


        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String userId = args[1];
        String userAttributePath = args[2];
        String userAttributeValue = args[3];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = updateUser(identitystore, identitystoreId, userId, userAttributePath, userAttributeValue);
        System.out.println("User" + result);
        identitystore.close();

    }

    // snippet-start:[identitystore.java2.update_user.main]
    public static String updateUser(IdentitystoreClient identitystore, String identitystoreId, String userId, String userAttributePath, String userAttributeValue) {
        try {

            String attributePath = userAttributePath;
            Document attributeValue = Document.fromString(userAttributeValue);

            AttributeOperation attributeOperation = AttributeOperation.builder()
                 .attributePath(attributePath)
                 .attributeValue(attributeValue)
                 .build();

            UpdateUserRequest request = UpdateUserRequest.builder()
                              .identityStoreId(identitystoreId)
                              .userId(userId)
                              .operations(attributeOperation)
                              .build();

            UpdateUserResponse response = identitystore.updateUser(request);

            System.out.format("User Field Name %s is updated with new value %s for userId %s\n", attributePath, attributeValue, userId);
            return "Updated";

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.update_user.main]
}
