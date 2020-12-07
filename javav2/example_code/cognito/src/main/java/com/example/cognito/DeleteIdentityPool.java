//snippet-sourcedescription:[DeleteIdentityPool.java demonstrates how to delete an existing Amazon Cognito identity pool.]
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

//snippet-start:[cognito.java2.deleteidpool.import]
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.DeleteIdentityPoolRequest;
//snippet-end:[cognito.java2.deleteidpool.import]

public class DeleteIdentityPool {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    DeleteIdentityPool <identityPoolId> \n\n" +
                "Where:\n" +
                "    identityPoolId - the Id value of your identity pool.\n\n" ;

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String identityPoold = args[0];
        CognitoIdentityClient cognitoIdclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        deleteIdPool(cognitoIdclient, identityPoold);
        cognitoIdclient.close();
    }

    //snippet-start:[cognito.java2.deleteidpool.main]
    public static void deleteIdPool(CognitoIdentityClient cognitoIdclient, String identityPoold) {
        try {

            DeleteIdentityPoolRequest identityPoolRequest = DeleteIdentityPoolRequest.builder()
                    .identityPoolId(identityPoold)
                    .build();

            cognitoIdclient.deleteIdentityPool(identityPoolRequest);
            System.out.println("Done");

        } catch (AwsServiceException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cognito.java2.deleteidpool.main]
}