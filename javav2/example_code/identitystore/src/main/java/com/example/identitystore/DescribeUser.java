//snippet-sourcedescription:[DescribeUser.java demonstrates how to retrieves the user metadata and attributes from the userId in an AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.describe_user.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.DescribeUserRequest;
import software.amazon.awssdk.services.identitystore.model.DescribeUserResponse;
// snippet-end:[Identitystore.java2.describe_user.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DescribeUser {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <userId>\n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userid - The id of the user. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }
        String identitystoreId = args[0];
        String userId = args[1];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = describeUser(identitystore, identitystoreId, userId);
        System.out.println("UserId  UserDisplayName: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.describe_user.main]
    public static String describeUser(IdentitystoreClient identitystore, String identitystoreID, String userId) {
        try {

            DescribeUserRequest request = DescribeUserRequest.builder()
                              .identityStoreId(identitystoreID)
                              .userId(userId)
                              .build();

            DescribeUserResponse response = identitystore.describeUser(request);

            return response.userId() + " " + response.displayName();
        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
    // snippet-end:[identitystore.java2.describe_user.main]
}
