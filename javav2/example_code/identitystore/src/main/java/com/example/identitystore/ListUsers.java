//snippet-sourcedescription:[ListUsers.java demonstrates how to get list of users in AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.identitystore;

// snippet-start:[Identitystore.java2.list_users.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.ListUsersRequest;
import software.amazon.awssdk.services.identitystore.model.ListUsersResponse;
import software.amazon.awssdk.services.identitystore.model.User;
// snippet-end:[Identitystore.java2.list_users.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListUsers {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "    <identitystoreId> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n\n" ;


        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }
        String identitystoreId = args[0];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        int result = listUsers(identitystore, identitystoreId);
        System.out.println("Total number of users is: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.list_users.main]
    public static int listUsers(IdentitystoreClient identitystore, String identitystoreId) {
        try {
            boolean done = false;
            int count = 0;
            String nextToken = null;

            while(!done) {
                ListUsersResponse response;
                if (nextToken == null){
                    ListUsersRequest request = ListUsersRequest.builder().identityStoreId(identitystoreId).build();
                    response = identitystore.listUsers(request);
                } else {
                    ListUsersRequest request = ListUsersRequest.builder().nextToken(nextToken).identityStoreId(identitystoreId).build();
                    response = identitystore.listUsers(request);
                }

                for(User user : response.users()) {
                    count ++;
                    System.out.format("UserName: %s, UserId: %s\n", user.userName(), user.userId());
                }

                nextToken = response.nextToken();
                if (nextToken == null){
                    done = true;
                }
            }
            return count;
        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return 0;
     }
     // snippet-end:[identitystore.java2.list_users.main]
}