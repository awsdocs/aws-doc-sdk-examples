/* Copyright 2010-2023 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
