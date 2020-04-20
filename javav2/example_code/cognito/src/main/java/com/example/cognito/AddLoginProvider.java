//snippet-sourcedescription:[AddLoginProvider.java demonstrates how to associate an identity pool with an identity provider.]
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

//snippet-start:[cognito.java2.add_login_provider.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityProvider;
import software.amazon.awssdk.services.cognitoidentity.model.UpdateIdentityPoolRequest;
import software.amazon.awssdk.services.cognitoidentity.model.UpdateIdentityPoolResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import java.util.HashMap;
//snippet-end:[cognito.java2.add_login_provider.import]

public class AddLoginProvider {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    AddLoginProvider <app_id> <identity_pool_name> <identity_pool_id>\n\n" +
                "Where:\n" +
                "    app_id - the application id from login provider.\n\n" +
                "    identity_pool_name - the name of your identity pool.\n\n" +
                "    identity_pool_id  - the Region and GUID of your id of your identity pool.\n\n" +
                "Example:\n" +
                "    CreateTable HelloTable\n";

         if (args.length < 3) {
             System.out.println(USAGE);
             System.exit(1);
         }

        /* Read the name from command args */
        String appId = args[0];
        String identityPoolName = args[1];
        String identityPoolId = args[2];

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        setLoginProvider(cognitoclient, appId, identityPoolName, identityPoolId) ;
    }

    //snippet-start:[cognito.java2.add_login_provider.main]
    public static void setLoginProvider(CognitoIdentityClient cognitoclient,
                                    String appId,
                                    String identityPoolName,
                                    String identityPoolId) {

        HashMap<String, String> potentialProviders = new HashMap<>();
        potentialProviders.put("facebook", "graph.facebook.com");
        potentialProviders.put("google", "accounts.google.com");
        potentialProviders.put("amazon", "www.amazon.com");
        potentialProviders.put("twitter", "api.twitter.com");
        potentialProviders.put("digits", "www.digits.com");

        HashMap<String, String> loginProvider = new HashMap<>();
        loginProvider.put(potentialProviders.get("amazon"), appId);

        try {

            UpdateIdentityPoolResponse response = cognitoclient
                .updateIdentityPool(
                        UpdateIdentityPoolRequest.builder()
                                .allowUnauthenticatedIdentities(false)
                                .identityPoolName(identityPoolName)
                                .identityPoolId(identityPoolId)
                                .supportedLoginProviders(loginProvider)
                                .build()
                );


            for (CognitoIdentityProvider cip : response.cognitoIdentityProviders()) {
                System.out.println("Client ID for " + cip.providerName() + " = " + cip.clientId());
             }
        } catch (CognitoIdentityProviderException e){
            e.getStackTrace();
        }
        //snippet-end:[cognito.java2.add_login_provider.main]
    }
}
