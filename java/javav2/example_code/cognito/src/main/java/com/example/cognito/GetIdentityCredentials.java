//snippet-sourcedescription:[GetIdentityCredentials.java demonstrates how to retrieve credentials for an Identity in an Identity Pool.]
//snippet-keyword:[Java]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Cognito]
// snippet-service:[cognito]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-06-22]
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
//snippet-start:[cognito.java2.GetIdentityCredentials.complete]

package com.example.cognito;

//snippet-start:[cognito.java2.GetIdentityCredentials.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityRequest;
import software.amazon.awssdk.services.cognitoidentity.model.GetCredentialsForIdentityResponse;
//snippet-end:[cognito.java2.GetIdentityCredentials.import]

public class GetIdentityCredentials {

    public static void main(String[] args) {
        final String USAGE = "\n" +
                "Usage:\n" +
                "    GetIdentityCredentials <identity_pool_id > \n\n" +
                "Where:\n" +
                "    identity_pool_id - The id of an existing identity pool.\n\n" +
                "Example:\n" +
                "    GetIdentityCredentials us-east-2:1234567890112-abcdefgc\n";

        if (args.length < 1) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String identity_pool_id = args[0];
        //snippet-start:[cognito.java2.GetIdentityCredentials.main]
        CognitoIdentityClient cognitoclient = CognitoIdentityClient.builder().region(Region.US_EAST_1).build();

        GetCredentialsForIdentityResponse response = cognitoclient.getCredentialsForIdentity(GetCredentialsForIdentityRequest.builder()
                .identityId(identity_pool_id)
                .build());

        System.out.println("Identity ID " + response.identityId() + ", Access Key Id " + response.credentials().accessKeyId() );

        //snippet-end:[cognito.java2.GetIdentityCredentials.main]
    }
}
//snippet-end:[cognito.java2.GetIdentityCredentials.complete]
