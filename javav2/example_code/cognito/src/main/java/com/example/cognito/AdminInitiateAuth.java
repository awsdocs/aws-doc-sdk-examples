// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.cognito;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminInitiateAuthRequest;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class AdminInitiateAuth {
    public static void main(String[] args) {
        final String usage = """

            Usage:
                <AuthFlow> <ClientId> <UserPoolId>

            Where:
                AuthFlow - The authentication flow for this call to run.\s
                ClientId - The app client ID.
                UserPoolId - The user name of the user whose registration you want to confirm.
            """;

        if (args.length != 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String authFlow = args[0];
        String clientId = args[1];
        String userPoolId = args[2];

        CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();
        adminInitiateAuth(identityProviderClient, authFlow, clientId, userPoolId);
        identityProviderClient.close();
    }

    public static void adminInitiateAuth(CognitoIdentityProviderClient identityProviderClient, String authFlow, String clientId, String userPoolId){
        AdminInitiateAuthRequest req = AdminInitiateAuthRequest.builder()
                .authFlow(authFlow)
                .clientId(clientId)
                .userPoolId(userPoolId)
                .build();

        identityProviderClient.adminInitiateAuth(req);
        System.out.println("Admin initiate auth");
    }
}