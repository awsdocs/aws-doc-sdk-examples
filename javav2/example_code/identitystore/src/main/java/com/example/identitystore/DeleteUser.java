//snippet-sourcedescription:[DeleteUser.java demonstrates how to delete a user within an AWS Identitystore given UserId.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.delete_user.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.DeleteUserRequest;
import software.amazon.awssdk.services.identitystore.model.DeleteUserResponse;
// snippet-end:[Identitystore.java2.delete_user.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class DeleteUser {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> <userId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userId - The id of the user to delete. \n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }
        String identitystoreId = args[0];
        String userId = args[1];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = deleteUser(identitystore, identitystoreId, userId);
        System.out.println("Successfully deleted the user: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.delete_user.main]
    public static String deleteUser(IdentitystoreClient identitystore, String identitystoreId, String userId) {
        try {

            DeleteUserRequest request = DeleteUserRequest.builder()
                              .identityStoreId(identitystoreId)
                              .userId(userId)
                              .build();

            DeleteUserResponse response = identitystore.deleteUser(request);

            return userId;

        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return "";
     }
     // snippet-end:[identitystore.java2.delete_user.main]
}
