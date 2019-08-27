//snippet-sourcedescription:[CreateIdentityPool.java demonstrates how to create a new identity pool. The identity pool is a store of user identity information that is specific to your AWS account.]
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
//snippet-start:[cognito.java2.create_identity_pool.complete]

package com.example.cognito;

//snippet-start:[cognito.java2.create_identity_pool.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.CreateIdentityPoolRequest;
import software.amazon.awssdk.services.cognitoidentity.model.CreateIdentityPoolResponse;
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
        //snippet-start:[cognito.java2.create_identity_pool.main]
        String identity_pool_name = args[1];

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        CreateIdentityPoolResponse response = cognitoclient.createIdentityPool(
                CreateIdentityPoolRequest.builder()
                        .allowUnauthenticatedIdentities(false)
                        .identityPoolName(identity_pool_name)
                        .build()
        );

        System.out.println("Unity Pool " + response.identityPoolName() + " is created. ID: " + response.identityPoolId());

        //snippet-end:[cognito.java2.create_identity_pool.main]
    }
}
//snippet-end:[cognito.java2.create_identity_pool.complete]