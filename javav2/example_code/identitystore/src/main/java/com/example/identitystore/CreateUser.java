//snippet-sourcedescription:[CreateUser.java demonstrates how to create a user within the specified AWS Identitystore.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Identitystore]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.identitystore;

// snippet-start:[Identitystore.java2.create_user.import]
import software.amazon.awssdk.services.identitystore.IdentitystoreClient;
import software.amazon.awssdk.services.identitystore.model.IdentitystoreException;
import software.amazon.awssdk.services.identitystore.model.CreateUserRequest;
import software.amazon.awssdk.services.identitystore.model.CreateUserResponse;
import software.amazon.awssdk.services.identitystore.model.Name;
// snippet-end:[Identitystore.java2.create_user.import]


/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class CreateUser {
    public static void main(String... args) {

        final String usage = "\n" +
        "Usage:\n" +
        "   <identitystoreId> <userName> <givenName> <familyName> \n\n" +
        "Where:\n" +
        "    identitystoreId - The id of the identitystore. \n" +
        "    userName - The name of the user to create. \n" +
        "    givenName - The first name of the user to create. \n" +
        "    familyName - The lastName of the user to create. \n\n" ;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String identitystoreId = args[0];
        String userName = args[1];
        String givenName = args[2];
        String familyName = args[3];

        IdentitystoreClient identitystore = IdentitystoreClient.builder().build();

        String result = createUser(identitystore, identitystoreId, userName, givenName, familyName);
        System.out.println("Successfully created user: " + result);
        identitystore.close();
    }

    // snippet-start:[identitystore.java2.create_user.main]
    public static String createUser(IdentitystoreClient identitystore, String identitystoreId, String userName, String givenName, String familyName) {
        try {

            String displayName = givenName + " " + familyName;

            Name name = Name.builder()
                 .givenName(givenName)
                 .familyName(familyName)
                 .build();

            CreateUserRequest request = CreateUserRequest.builder()
                              .identityStoreId(identitystoreId)
                              .userName(userName)
                              .displayName(displayName) 
                              .name(name)
                              .build();

            CreateUserResponse response = identitystore.createUser(request);

            return response.userId();
        } catch (IdentitystoreException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return "";
     }
     // snippet-end:[identitystore.java2.create_user.main]
}