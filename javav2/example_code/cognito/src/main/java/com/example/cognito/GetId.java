//snippet-sourcedescription:[GetId.java demonstrates how to retrieve the client ID from an identity provider.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cognito]
// snippet-service:[cognito]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[4/16/2020]
// snippet-sourceauthor:[scmacdon - (AWS)]

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

//snippet-start:[cognito.java2.GetId.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import java.util.HashMap;
//snippet-end:[cognito.java2.GetId.import]

public class GetId {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetId <app_id> <identity_pool_id><cognitoUserPool >\n\n" +
                "Where:\n" +
                "    appId - the application ID from the login provider\n\n" +
                "    identityPoolId  - the AWS Region and GUID of the ID of your identity pool\n\n" +
                "    cognitoUserPool  - the user pool\n\n" +
                "Example:\n" +
                "    GetId amzn1.application-oa2-client.1234567890112-abcdefg us-east-2:1234567890112-abcdefg\n";

        if (args.length < 3) {
             System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String appId = args[0];
        String identityPoolId = args[1];
        String cognitoUserPool = args[2];

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        getClientID(cognitoclient, appId, identityPoolId, cognitoUserPool);
    }

    //snippet-start:[cognito.java2.GetID.main]
    public static void getClientID(CognitoIdentityClient cognitoclient,
                                   String appId,
                                   String identityPoolId,
                                   String cognitoUserPool){

        HashMap<String, String> potentialProviders = new HashMap<>();
        potentialProviders.put("facebook", "graph.facebook.com");
        potentialProviders.put("google", "accounts.google.com");
        potentialProviders.put("amazon", "www.amazon.com");
        potentialProviders.put("twitter", "api.twitter.com");
        potentialProviders.put("digits", "www.digits.com");

        HashMap<String, String> loginProvider = new HashMap<>();
        loginProvider.put(potentialProviders.get("amazon"), appId);

        HashMap login = new HashMap<String, String>();
        login.put(cognitoUserPool, appId);

        try {
        GetIdResponse response = cognitoclient.getId(GetIdRequest.builder()
                .identityPoolId(identityPoolId)
                .logins(login)
                .build());

        System.out.println("Identity ID " + response.identityId());

        } catch (CognitoIdentityProviderException e){
            e.getStackTrace();
        }
        //snippet-end:[cognito.java2.GetID.main]
    }
}
