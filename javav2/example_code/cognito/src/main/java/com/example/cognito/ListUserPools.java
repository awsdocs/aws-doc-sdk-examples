//snippet-sourcedescription:[ListUserPools.java demonstrates how to to list existing users in the specified user pool.]
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

//snippet-start:[cognito.java2.ListUserPools.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUserPoolsRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserPoolDescriptionType;
//snippet-end:[cognito.java2.ListUserPools.import]

public class ListUserPools {

    public static void main(String[] args) {

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllUserPools(cognitoclient) ;
        cognitoclient.close();
    }

    //snippet-start:[cognito.java2.ListUserPools.main]
    public static void listAllUserPools(CognitoIdentityProviderClient cognitoclient ) {

        try {
            ListUserPoolsResponse response = cognitoclient
                    .listUserPools(
                            ListUserPoolsRequest.builder()
                                    .maxResults(10)
                                    .build()
                    );

            for (UserPoolDescriptionType userpool : response.userPools()) {
                System.out.println("User pool " + userpool.name() + ", User ID " + userpool.id() + ", Status " + userpool.status());
            }
        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[cognito.java2.ListUserPools.main]
    }
}