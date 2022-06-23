//snippet-sourcedescription:[DeleteUserPool.java demonstrates how to delete an existing user pool.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[05/18/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cognito;

//snippet-start:[cognito.java2.DeleteUserPool.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserPoolRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeleteUserPoolResponse;
//snippet-end:[cognito.java2.DeleteUserPool.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteUserPool {

    public static void main(String[] args) {

        final String usage = "\n" +
                "Usage:\n" +
                "    <userPoolId> \n\n" +
                "Where:\n" +
                "    userPoolId - The Id value given to your user pool.\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String userPoolId = args[0];
        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        deletePool(cognitoClient, userPoolId);
        cognitoClient.close();
    }

    //snippet-start:[cognito.java2.DeleteUserPool.main]
    public static void deletePool(CognitoIdentityProviderClient cognitoClient, String userPoolId ) {

        try {
            DeleteUserPoolRequest request = DeleteUserPoolRequest.builder()
                    .userPoolId(userPoolId)
                    .build();

            DeleteUserPoolResponse response = cognitoClient.deleteUserPool(request);
            System.out.println("User pool " + response.toString() + " deleted. ID: " + request.userPoolId());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
     }
    //snippet-end:[cognito.java2.DeleteUserPool.main]
}