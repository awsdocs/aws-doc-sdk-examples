//snippet-sourcedescription:[ListUserPoolClients.java demonstrates how to list existing user pool clients that are available in the specified AWS Region in your current AWS account.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/04/2020]
//snippet-sourceauthor:[scmacdon AWS]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cognito;

//snippet-start:[cognito.java2.ListUserPoolClients.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolClientsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolClientDescription;
//snippet-end:[cognito.java2.ListUserPoolClients.import]

/**
 * To run this AWS code example, ensure that you have setup your development environment, including your AWS credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

public class ListUserPoolClients {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "    ListUserPoolClients <userPoolId> \n\n" +
                "Where:\n" +
                "    userPoolId - the ID given to your user pool.\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String userPoolId = args[0];
        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllUserPoolClients(cognitoclient, userPoolId ) ;
        cognitoclient.close();
    }

    //snippet-start:[cognito.java2.ListUserPoolClients.main]
    public static void listAllUserPoolClients(CognitoIdentityProviderClient cognitoclient, String userPoolId) {

        try {
            ListUserPoolClientsResponse response = cognitoclient.listUserPoolClients(ListUserPoolClientsRequest.builder()
                    .userPoolId(userPoolId)
                    .build());

            for(UserPoolClientDescription userPoolClient : response.userPoolClients()) {
                System.out.println("User pool client " + userPoolClient.clientName() + ", Pool ID " + userPoolClient.userPoolId() + ", Client ID " + userPoolClient.clientId() );
            }

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[cognito.java2.ListUserPoolClients.main]
    }
}