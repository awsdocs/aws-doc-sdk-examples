//snippet-sourcedescription:[AddLoginProvider.java demonstrates how to associate an identity pool with an identity provider.]
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
//snippet-start:[cognito.java2.add_login_provider.complete]

package com.example.cognito;
//snippet-start:[cognito.java2.add_login_provider.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.CognitoIdentityProvider;
import software.amazon.awssdk.services.cognitoidentity.model.UpdateIdentityPoolRequest;
import software.amazon.awssdk.services.cognitoidentity.model.UpdateIdentityPoolResponse;

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

        //snippet-start:[cognito.java2.add_login_provider.main]
        /* Read the name from command args */
        String app_id = args[0];
        String identity_pool_name = args[1];
        String identity_pool_id = args[2];

        HashMap<String, String> potential_providers = new HashMap<>();
        potential_providers.put("facebook", "graph.facebook.com");
        potential_providers.put("google", "accounts.google.com");
        potential_providers.put("amazon", "www.amazon.com");
        potential_providers.put("twitter", "api.twitter.com");
        potential_providers.put("digits", "www.digits.com");

        HashMap<String, String> login_provider = new HashMap<>();
        login_provider.put(potential_providers.get("amazon"), app_id);

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        UpdateIdentityPoolResponse response = cognitoclient
                .updateIdentityPool(
                        UpdateIdentityPoolRequest.builder()
                                .allowUnauthenticatedIdentities(false)
                                .identityPoolName(identity_pool_name)
                                .identityPoolId(identity_pool_id)
                                .supportedLoginProviders(login_provider)
                                .build()
                );

        for (CognitoIdentityProvider cip : response.cognitoIdentityProviders()) {
            System.out.println("Client ID for " + cip.providerName() + " = " + cip.clientId());
        }
        //snippet-end:[cognito.java2.add_login_provider.main]
    }
}
//snippet-end:[cognito.java2.add_login_provider.complete]