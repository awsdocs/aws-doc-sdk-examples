//snippet-sourcedescription:[DeleteIdentityPool.java demonstrates how to delete an existing Amazon Cognito identity pool.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Amazon Cognito]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.cognito;

//snippet-start:[cognito.java2.deleteidpool.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.DeleteIdentityPoolRequest;
//snippet-end:[cognito.java2.deleteidpool.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteIdentityPool {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <identityPoolId> \n\n" +
            "Where:\n" +
            "    identityPoolId - The Id value of your identity pool.\n\n" ;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String identityPoold = args[0];
        CognitoIdentityClient cognitoIdClient = CognitoIdentityClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        deleteIdPool(cognitoIdClient, identityPoold);
        cognitoIdClient.close();
    }

    //snippet-start:[cognito.java2.deleteidpool.main]
    public static void deleteIdPool(CognitoIdentityClient cognitoIdClient, String identityPoold) {
        try {

            DeleteIdentityPoolRequest identityPoolRequest = DeleteIdentityPoolRequest.builder()
                .identityPoolId(identityPoold)
                .build();

            cognitoIdClient.deleteIdentityPool(identityPoolRequest);
            System.out.println("Done");

        } catch (AwsServiceException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cognito.java2.deleteidpool.main]
}