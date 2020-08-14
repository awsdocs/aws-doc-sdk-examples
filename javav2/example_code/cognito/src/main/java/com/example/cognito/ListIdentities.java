//snippet-sourcedescription:[ListIdentities.java demonstrates how list identifies that belong to an Amazon Cognito identity pool.]
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

//snippet-start:[cognito.java2.listidentities.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.IdentityDescription;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentitiesRequest;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
//snippet-end:[cognito.java2.listidentities.import]

import java.util.List;

public class ListIdentities {

    public static void main(String[] args) {

        final String USAGE = "\n" +
                "Usage:\n" +
                "    ListIdentities<identity_pool_id>>\n\n" +
                "Where:\n" +
                "    identityPoolId  - the Region and GUID of your id of your identity pool.\n\n" +
                "Example:\n" +
                "    ListIdentities  us-east-1:00eb915b-c521-417b-af0d-ebad008axxxx\n";

        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        /* Read the name from command args */
        String identityPoolId = args[0];

        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listPoolIdentities(cognitoclient, identityPoolId);
    }

    //snippet-start:[cognito.java2.listidentities.main]
    public static void listPoolIdentities(CognitoIdentityClient cognitoclient, String identityPoolId) {

        try {
            ListIdentitiesRequest identitiesRequest = ListIdentitiesRequest.builder()
                .identityPoolId(identityPoolId)
                .maxResults(15)
                .build() ;

            ListIdentitiesResponse response = cognitoclient.listIdentities(identitiesRequest);
            List<IdentityDescription> identities = response.identities();

            for (IdentityDescription identity: identities) {
                System.out.println("The ID is : "+identity.identityId());
            }
        } catch (CognitoIdentityProviderException e){
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
        }
    }
    //snippet-end:[cognito.java2.listidentities.main]
 }
