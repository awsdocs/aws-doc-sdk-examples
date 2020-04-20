//snippet-sourcedescription:[ListUserPoolClients.java demonstrates how list existing User Pool Clients available in the specified region in your current AWS account.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/16/2020]
//snippet-sourceauthor:[scmacdon-AWS]
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
//snippet-start:[cognito.java2.ListUserPoolClients.complete]

package com.example.cognito;

//snippet-start:[cognito.java2.ListUserPoolClients.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolClientDescription;
//snippet-end:[cognito.java2.ListUserPoolClients.import]

public class ListUserPoolClients {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListUserPoolClients <user_pool_id> \n\n" +
                "Where:\n" +
                "    userPoolId - The id given your user pool when created.\n\n" +
                "Example:\n" +
                "    ListUserPoolClients us-east-2_P0oL1D\n";

        if (args.length < 1) {
             System.out.println(USAGE);
             System.exit(1);
         }

        String userPoolId = args[0];

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllUserPoolClients(cognitoclient, userPoolId ) ;
    }

    //snippet-start:[cognito.java2.ListUserPoolClients.main]
    public static void listAllUserPoolClients(CognitoIdentityProviderClient cognitoclient, String userPoolId) {

        try {

            ListUserPoolClientsResponse response = cognitoclient.listUserPoolClients(ListUserPoolClientsRequest.builder()
                .userPoolId(userPoolId)
                .build());

            for(UserPoolClientDescription userPoolClient : response.userPoolClients()) {
                System.out.println("User Pool Client " + userPoolClient.clientName() + ", Pool Id " + userPoolClient.userPoolId() + ", Client Id " + userPoolClient.clientId() );
            }
    } catch (
    CognitoIdentityProviderException e){
        e.getStackTrace();
    }
        //snippet-end:[cognito.java2.ListUserPoolClients.main]
    }
}
