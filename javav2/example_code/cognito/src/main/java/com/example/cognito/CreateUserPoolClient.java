//snippet-sourcedescription:[CreateUserPoolClient.java demonstrates how to create a user pool client for Amazon Cognito.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Cognito]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cognito;

//snippet-start:[cognito.java2.user_pool.create_user_pool_client.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientResponse;
//snippet-end:[cognito.java2.user_pool.create_user_pool_client.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateUserPoolClient {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <clientName> <userPoolId> \n\n" +
            "Where:\n" +
            "    clientName - The name for the user pool client to create.\n\n" +
            "    userPoolId - The ID for the user pool.\n\n" ;

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String clientName = args[0];
        String userPoolId = args[1];
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createPoolClient (cognitoClient, clientName, userPoolId) ;
        cognitoClient.close();
    }

    //snippet-start:[cognito.java2.user_pool.create_user_pool_client.main]
    public static void createPoolClient ( CognitoIdentityProviderClient cognitoClient, String clientName, String userPoolId) {

        try {
            CreateUserPoolClientRequest request = CreateUserPoolClientRequest.builder()
                .clientName(clientName)
                .userPoolId(userPoolId)
                .build();

            CreateUserPoolClientResponse response = cognitoClient.createUserPoolClient(request);
            System.out.println("User pool " + response.userPoolClient().clientName() + " created. ID: " + response.userPoolClient().clientId());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cognito.java2.user_pool.create_user_pool_client.main]
}