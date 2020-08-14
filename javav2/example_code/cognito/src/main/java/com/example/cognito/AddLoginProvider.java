//snippet-sourcedescription:[AddLoginProvider.java demonstrates how to associate an identity pool with an identity provider.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-service:[cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[8/14/2020]
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
import java.util.ArrayList;
import java.util.List;
//snippet-end:[cognito.java2.add_login_provider.import]

public class AddLoginProvider {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    AddLoginProvider <app_id> <identity_pool_name> <identity_pool_id><providerName>\n\n" +
                "Where:\n" +
                "    appId - the application id from login provider.\n\n" +
                "    identityPoolName - the name of your identity pool.\n\n" +
                "    identityPoolId  - the Region and GUID of your id of your identity pool.\n\n" +
                "    providerName  - the provider name (ie, cognito-idp.us-east-1.amazonaws.com/us-east-1_Taz4Yxxxx).\n\n";

        if (args.length < 4) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String appId = args[0];
        String identityPoolName = args[1];
        String identityPoolId = args[2];
        String providerName = args[3];

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        setLoginProvider(cognitoclient,appId, identityPoolName, identityPoolId, providerName) ;
    }

    //snippet-start:[cognito.java2.add_login_provider.main]
    public static void setLoginProvider(CognitoIdentityClient cognitoclient,
                                    String appId,
                                    String identityPoolName,
                                    String identityPoolId,
                                    String providerName) {

        CognitoIdentityProvider identityProvider = CognitoIdentityProvider.builder()
                .providerName(providerName)
                .clientId(appId)
                .build();

        List<CognitoIdentityProvider> proList = new ArrayList<>();
        proList.add(identityProvider);

        try {

            UpdateIdentityPoolRequest poolRequest = UpdateIdentityPoolRequest.builder()
                    .allowUnauthenticatedIdentities(true)
                    .identityPoolName(identityPoolName)
                    .identityPoolId(identityPoolId)
                    .cognitoIdentityProviders(proList)
                    .build() ;

            UpdateIdentityPoolResponse response = cognitoclient.updateIdentityPool(poolRequest);
            List<CognitoIdentityProvider> providers = response.cognitoIdentityProviders();

            for (CognitoIdentityProvider provider: providers) {
                System.out.println("The client ID is : "+provider.clientId());
                System.out.println("The provider name is : "+provider.providerName());
            }

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        //snippet-end:[cognito.java2.add_login_provider.main]
    }
}
