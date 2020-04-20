//snippet-sourcedescription:[CreateUserPool.java demonstrates how to create a user pool for Amazon Cognito.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[4/16/2020]
//snippet-sourceauthor:[scmacdon - AWS]
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

//snippet-start:[cognito.java2.create_user_pool.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CreateUserPoolResponse;
//snippet-end:[cognito.java2.create_user_pool.import]

public class CreateUserPool {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    CreateUserPool <userPoolName> \n\n" +
                "Where:\n" +
                "    userPoolName - The name to give your user pool when created.\n\n" +
                "Example:\n" +
                "    CreateTable HelloTable\n";

        if (args.length < 1) {
               System.out.println(USAGE);
               System.exit(1);
         }
        /* Read the name from command args */
        String userPoolName = args[0];

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        String id = createPool(cognitoclient,userPoolName);
        System.out.println("User Pool ID: " + id);
    }

    //snippet-start:[cognito.java2.create_user_pool.main]
    public static String createPool(CognitoIdentityProviderClient cognitoclient,String userPoolName ) {

        try {
            CreateUserPoolResponse repsonse = cognitoclient.createUserPool(
                    CreateUserPoolRequest.builder()
                            .poolName(userPoolName)
                            .build()
            );

           return repsonse.userPool().id();

        } catch (CognitoIdentityProviderException e){
            e.getStackTrace();
        }
        return "";
        //snippet-end:[cognito.java2.create_user_pool.main]
    }
}
