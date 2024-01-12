// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.cognito;

// snippet-start:[cognito.java2.listidentities.main]
// snippet-start:[cognito.java2.listidentities.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentitiesRequest;
import software.amazon.awssdk.services.cognitoidentity.model.ListIdentitiesResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
// snippet-end:[cognito.java2.listidentities.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListIdentities {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <identityPoolId>

                Where:
                    identityPoolId - The id value of your identity pool (for example, us-east-1:00eb915b-c521-417b-af0d-ebad008axxxx).
                """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String identityPoolId = args[0];
        CognitoIdentityClient cognitoClient = CognitoIdentityClient.builder()
                .region(Region.US_EAST_1)
                .build();

        listPoolIdentities(cognitoClient, identityPoolId);
        cognitoClient.close();
    }

    public static void listPoolIdentities(CognitoIdentityClient cognitoClient, String identityPoolId) {
        try {
            ListIdentitiesRequest identitiesRequest = ListIdentitiesRequest.builder()
                    .identityPoolId(identityPoolId)
                    .maxResults(15)
                    .build();

            ListIdentitiesResponse response = cognitoClient.listIdentities(identitiesRequest);
            response.identities().forEach(identity -> {
                System.out.println("The ID is : " + identity.identityId());
            });

        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[cognito.java2.listidentities.main]