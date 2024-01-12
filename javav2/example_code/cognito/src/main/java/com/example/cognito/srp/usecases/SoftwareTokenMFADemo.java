// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.cognito.srp.usecases;

import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

public class SoftwareTokenMFADemo {
    private final CognitoIdentityProviderClient cognitoClient;

    public SoftwareTokenMFADemo(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public AssociateSoftwareTokenResponse associateSoftwareToken(String session) {
        AssociateSoftwareTokenRequest associationRequest = AssociateSoftwareTokenRequest.builder()
                .session(session)
                .build();

        try {
            AssociateSoftwareTokenResponse response = cognitoClient.associateSoftwareToken(associationRequest);
            return response;
        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

    public VerifySoftwareTokenResponse verifySoftwareToken(String session, String code) {
        VerifySoftwareTokenRequest verifySoftwareTokenRequest = VerifySoftwareTokenRequest.builder()
                .session(session)
                .userCode(code)
                .build();

        try {
            VerifySoftwareTokenResponse response = cognitoClient.verifySoftwareToken(verifySoftwareTokenRequest);
            return response;
        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }

}
