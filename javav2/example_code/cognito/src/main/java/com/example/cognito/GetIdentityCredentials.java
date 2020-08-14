//snippet-sourcedescription:[GetIdentityCredentials.java demonstrates how to retrieve credentials for an Identity in an Identity Pool.]
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

//snippet-start:[cognito.java2.GetIdentityCredentials.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
//snippet-end:[cognito.java2.GetIdentityCredentials.import]

public class GetIdentityCredentials {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetIdentityCredentials <identity_id > \n\n" +
                "Where:\n" +
                "    identityId - The id of an existing identity.\n\n" +
                "Example:\n" +
                "    GetIdentityCredentials us-east-2:1234567890000-abcdefgc\n";

         if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(1);
         }

        String identityId = args[0];

        // Create a CognitoIdentityClient instance
        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        getCredsForIdentity(cognitoclient, identityId);
    }

    //snippet-start:[cognito.java2.GetIdentityCredentials.main]
    public static void getCredsForIdentity(CognitoIdentityClient cognitoclient, String identityId) {

         try {
                GetCredentialsForIdentityRequest getCredentialsForIdentityRequest = GetCredentialsForIdentityRequest.builder()
                        .identityId(identityId)
                        .build();

                GetCredentialsForIdentityResponse response = cognitoclient.getCredentialsForIdentity(getCredentialsForIdentityRequest);
                System.out.println("Identity ID " + response.identityId() + ", Access Key Id " + response.credentials().accessKeyId());

            } catch (CognitoIdentityProviderException e) {
                System.err.println(e.awsErrorDetails().errorMessage());
                System.exit(1);
            }
            //snippet-end:[cognito.java2.GetIdentityCredentials.main]
   }
}
