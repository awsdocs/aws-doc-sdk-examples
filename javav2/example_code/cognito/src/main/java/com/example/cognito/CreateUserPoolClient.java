//snippet-sourcedescription:[CreateUserPoolClient.java demonstrates how to create a user pool client for Amazon Cognito.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/16/2020]
//snippet-sourceauthor:[scmacdon - AWS]
/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.cognito;

//snippet-start:[cognito.java2.user_pool.create_user_pool_client.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientResponse;
//snippet-end:[cognito.java2.user_pool.create_user_pool_client.import]

public class CreateUserPoolClient {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateUserPoolClient <clientName> <userPoolId> \n\n" +
                "Where:\n" +
                "    clientName - the client name for the user pool client to create\n\n" +
                "    userPoolId - the user pool ID for the user pool in which to create a user pool client\n\n" +
                "Example:\n" +
                "    CreateUserPoolClient client1 client1IdNum\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
       }

        /* Read the name from command args */
        String clientName = args[0];
        String userPoolId = args[1];

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        createPoolClient (cognitoclient, clientName, userPoolId) ;
    }

    //snippet-start:[cognito.java2.user_pool.create_user_pool_client.main]
    public static void createPoolClient ( CognitoIdentityProviderClient cognitoclient,
                                          String clientName,
                                          String userPoolId ) {

        try {

            CreateUserPoolClientResponse repsonse = cognitoclient.createUserPoolClient(
                CreateUserPoolClientRequest.builder()
                        .clientName(clientName)
                        .userPoolId(userPoolId)
                        .build()
        );

            System.out.println("User Pool " + repsonse.userPoolClient().clientName() + " created. ID: " + repsonse.userPoolClient().clientId());

        } catch (CognitoIdentityProviderException e){
            e.getStackTrace();
        }
        //snippet-end:[cognito.java2.user_pool.create_user_pool_client.main]
    }
}
