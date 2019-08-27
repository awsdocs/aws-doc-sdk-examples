//snippet-sourcedescription:[GetId.java demonstrates how to retrieve the ClientID from an identity provider.]
//snippet-keyword:[Java]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cognito]
// snippet-service:[cognito]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-06-29]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

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
//snippet-start:[cognito.java2.GetId.complete]

package com.example.cognito;
//snippet-start:[cognito.java2.GetId.import]

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetIdResponse;

import java.util.HashMap;
//snippet-end:[cognito.java2.GetId.import]

public class GetId {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetId <app_id> <identity_pool_id>\n\n" +
                "Where:\n" +
                "    app_id - the application id from login provider.\n\n" +
                "    identity_pool_id  - the Region and GUID of your id of your identity pool.\n\n" +
                "Example:\n" +
                "    GetId amzn1.application-oa2-client.1234567890112-abcdefg us-east-2:1234567890112-abcdefg\n";

        if (args.length < 3) {
            System.out.println(USAGE);
            System.exit(1);
        }

        //snippet-start:[cognito.java2.GetID.main]
        /* Read the name from command args */
        String app_id = args[0];
        String identity_pool_id = args[1];

        HashMap<String, String> potential_providers = new HashMap<>();
        potential_providers.put("facebook", "graph.facebook.com");
        potential_providers.put("google", "accounts.google.com");
        potential_providers.put("amazon", "www.amazon.com");
        potential_providers.put("twitter", "api.twitter.com");
        potential_providers.put("digits", "www.digits.com");

        HashMap<String, String> login_provider = new HashMap<>();
        login_provider.put(potential_providers.get("amazon"), app_id);
        String cognito_user_pool = "cognito-idp.us-east-1.amazonaws.com/";
        HashMap login = new HashMap<String, String>();
        login.put(cognito_user_pool, app_id);

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder().region(Region.US_EAST_1).build();

        GetIdResponse response = cognitoclient.getId(GetIdRequest.builder()
                .identityPoolId(identity_pool_id)
                .logins(login)
                .build());

        System.out.println("Identity ID " + response.identityId());
        //snippet-end:[cognito.java2.GetID.main]

    }
}
//snippet-end:[cognito.java2.GetId.complete]