//snippet-sourcedescription:[GetUserId.java demonstrates how to retrieve the userid based on other unique keys from AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.get_userid.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.GetUserIdRequest;
import software.amazon.awssdk.services.identitystore.model.GetUserIdResponse;
import software.amazon.awssdk.services.identitystore.model.AlternateIdentifier;
import software.amazon.awssdk.services.identitystore.model.UniqueAttribute;
import software.amazon.awssdk.core.document.Document;
// snippet-end:[Identitystore.java2.get_userid.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class GetUserId {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <userAttributeName> <userAttributeValue> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userAttributeName - The name of the unique attribute of the user. \n" +
        "    userAttributeValue - The value of the specified user attribute. \n\n" ;


        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String userAttributeName = args[1];
        String userAttributeValue = args[2];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = getUserId(identitystore, identitystoreId, userAttributeName, userAttributeValue);
        System.out.println("UserId: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.get_userid.main]
    public static String getUserId(IdentitystoreClient identitystore, String identitystoreId, String userAttributeName, String userAttributeValue) {
        try {

            String attributePath = userAttributeName;
            Document attributeValue = Document.fromString(userAttributeValue);

            UniqueAttribute uniqueAttribute = UniqueAttribute.builder()
                 .attributePath(attributePath)
                 .attributeValue(attributeValue)
                 .build();

            AlternateIdentifier alternateIdentifier = AlternateIdentifier.builder()
                    .uniqueAttribute(uniqueAttribute)
                    .build();

            GetUserIdRequest request = GetUserIdRequest.builder()
                              .identityStoreId(identitystoreId)
                              .alternateIdentifier(alternateIdentifier)
                              .build();

            GetUserIdResponse response = identitystore.getUserId(request);
            return response.userId();

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
     }
      // snippet-end:[identitystore.java2.get_userid.main]
}