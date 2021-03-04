//snippet-sourcedescription:[DeleteUserPool.java demonstrates how to delete an existing user pool.]
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

//snippet-start:[cognito.java2.DeleteUserPool.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserPoolRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserPoolResponse;
//snippet-end:[cognito.java2.DeleteUserPool.import]

public class DeleteUserPool {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteUserPool <userPoolId> \n\n" +
                "Where:\n" +
                "    userPoolId - the Id value given to your user pool.\n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String userPoolId = args[0];
        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deletePool(cognitoclient, userPoolId);
        cognitoclient.close();
    }

    //snippet-start:[cognito.java2.DeleteUserPool.main]
    public static void deletePool(CognitoIdentityProviderClient cognitoclient, String userPoolId ) {

        try {
            DeleteUserPoolRequest request = DeleteUserPoolRequest.builder()
                    .userPoolId(userPoolId)
                    .build();

            DeleteUserPoolResponse response = cognitoclient.deleteUserPool(request);
            System.out.println("User pool " + response.toString() + " deleted. ID: " + request.userPoolId());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[cognito.java2.DeleteUserPool.main]
    }
}