//snippet-sourcedescription:[CreateIdentityPool.java demonstrates how to create a new identity pool. The identity pool is a store of user identity information that is specific to your AWS account.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/16/2020]
//snippet-sourceauthor:[scmacdon AWS]
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

//snippet-start:[cognito.java2.create_identity_pool.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.CreateIdentityPoolRequest;
import software.amazon.awssdk.services.cognitoidentity.model.CreateIdentityPoolResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
//snippet-end:[cognito.java2.create_identity_pool.import]

public class CreateIdentityPool {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateIdentityPool <identity_pool_name> \n\n" +
                "Where:\n" +
                "    identity_pool_name - the name to give your identity pool.\n\n" +
                "Example:\n" +
                "    CreateTable HelloTable\n";

        if (args.length < 1) {
            System.out.println(USAGE);
           System.exit(1);
        }

        String identityPoolName = args[0];

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String identityPoolId = createIdPool(cognitoclient, identityPoolName) ;
        System.out.println("Unity Pool Id " + identityPoolId);
    }

    //snippet-start:[cognito.java2.create_identity_pool.main]
    public static String createIdPool(CognitoIdentityClient cognitoclient, String identityPoolName ) {

        try {
            CreateIdentityPoolResponse response = cognitoclient.createIdentityPool(
                CreateIdentityPoolRequest.builder()
                        .allowUnauthenticatedIdentities(false)
                        .identityPoolName(identityPoolName)
                        .build()
        );

           return response.identityPoolId();
    } catch (CognitoIdentityProviderException e){
        e.getStackTrace();
    }
       return "";
        //snippet-end:[cognito.java2.create_identity_pool.main]
    }
}
