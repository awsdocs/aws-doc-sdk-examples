//snippet-sourcedescription:[ListUserPools.java demonstrates how to to list existing users in the specified user pool.]
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
        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListUserPools  \n\n" +
                "Example:\n" +
                "    ListUserPools \n";

        CognitoIdentityProviderClient cognitoclient = CognitoIdentityProviderClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listAllUserPools(cognitoclient ) ;
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
               System.out.println("UserPool " + userpool.name() + ", User ID " + userpool.id() + ", Status " + userpool.status());
           }
        } catch (CognitoIdentityProviderException e){
            e.getStackTrace();
        }
        //snippet-end:[cognito.java2.ListUserPools.main]
    }
}
