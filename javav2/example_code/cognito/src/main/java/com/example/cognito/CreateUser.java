// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cognito;

// snippet-start:[cognito.java2.new_admin_user.main]
// snippet-start:[cognito.java2.new_admin_user.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
// snippet-end:[cognito.java2.new_admin_user.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateUser {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <userPoolId> <userName> <email> <password>

                Where:
                    userPoolId - The Id value for the user pool where the user is created.
                    userName - The user name for the new user.
                    email - The email to use for verifying the user.
                    password - The password for this user.
                """;

        if (args.length != 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String userPoolId = args[0];
        String userName = args[1];
        String email = args[2];
        String password = args[3];

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createNewUser(cognitoClient, userPoolId, userName, email, password);
        cognitoClient.close();
    }

    public static void createNewUser(CognitoIdentityProviderClient cognitoClient,
            String userPoolId,
            String name,
            String email,
            String password) {

        try {
            AttributeType userAttrs = AttributeType.builder()
                    .name("email")
                    .value(email)
                    .build();

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(name)
                    .temporaryPassword(password)
                    .userAttributes(userAttrs)
                    .messageAction("SUPPRESS")
                    .build();

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);
            System.out.println(
                    "User " + response.user().username() + "is created. Status: " + response.user().userStatus());

        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cognito.java2.new_admin_user.main]