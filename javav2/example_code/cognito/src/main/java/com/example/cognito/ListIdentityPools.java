//snippet-sourcedescription:[ListIdentityPools.java demonstrates how to list Amazon Cognito identity pools.]
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

//snippet-start:[cognito.java2.listidentitypools.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.IdentityPoolShortDescription;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentityPoolsRequest;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentityPoolsResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
//snippet-end:[cognito.java2.listidentitypools.import]

import java.util.List;

public class ListIdentityPools {

    public static void main(String[] args) {

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listIdPools(cognitoclient);
        cognitoclient.close();
    }

    //snippet-start:[cognito.java2.listidentitypools.main]
    public static void listIdPools(CognitoIdentityClient cognitoclient) {

        try {

            ListIdentityPoolsRequest poolsRequest = ListIdentityPoolsRequest.builder()
                    .maxResults(15)
                    .build();

            ListIdentityPoolsResponse poolReponse = cognitoclient.listIdentityPools(poolsRequest);
            List<IdentityPoolShortDescription> pools = poolReponse.identityPools();

            for (IdentityPoolShortDescription pool: pools) {
                System.out.println("Pool ID: "+pool.identityPoolId());
                System.out.println("Pool name: "+pool.identityPoolName());
            }

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[cognito.java2.listidentitypools.main]
}