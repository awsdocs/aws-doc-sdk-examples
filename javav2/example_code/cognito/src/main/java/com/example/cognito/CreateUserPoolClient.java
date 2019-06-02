//snippet-sourcedescription:[CreateUserPoolClient.java demonstrates how to create a user pool for Cognito.]
//snippet-keyword:[Java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2019-06-02]
//snippet-sourceauthor:[jschwarzwalder AWS]
/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[cognito.java2.user_pool.create_user_pool_client.complete]

package com.example.cognito;

//snippet-start:[cognito.java2.user_pool.create_user_pool_client.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolClientResponse;
//snippet-end:[cognito.java2.user_pool.create_user_pool_client.import]

public class CreateUserPoolClient {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateUserPoolClient <client_name> <user_pool_id> \n\n" +
                "Where:\n" +
                "    client_name - The client name for the user pool client you would like to create.\n\n" +
                "    user_pool_id - The user pool ID for the user pool where you want to create a user pool client.\n\n" +
                "Example:\n" +
                "    CreateTable HelloTable\n";

        if (args.length < 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        //snippet-start:[cognito.java2.user_pool.create_user_pool_client.main]
        /* Read the name from command args */
        String client_name = args[0];
        String user_pool_id = args[1];

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder().region(Region.US_EAST_1).build();

        CreateUserPoolClientResponse repsonse = cognitoclient.createUserPoolClient(CreateUserPoolClientRequest.builder()
                .clientName(client_name)
                .userPoolId(user_pool_id)
                .build());

        System.out.println("User Pool " + repsonse.userPoolClient().clientName() + " created. ID: " + repsonse.userPoolClient().clientId());

        //snippet-end:[cognito.java2.user_pool.create_user_pool_client.main]
    }
}
//snippet-end:[cognito.java2.user_pool.create_user_pool_client.complete]