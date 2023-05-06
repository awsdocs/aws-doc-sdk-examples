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

import java.util.HashMap;
import java.util.Map;

import com.example.cognito.srp.utils.HashUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.utils.StringUtils;


public class AdminAuthDemo {

    private final CognitoIdentityProviderClient cognitoClient;
    private final String poolId;
    private final String clientId;
    private final String clientSecret;

    public AdminAuthDemo(CognitoIdentityProviderClient cognitoClient, String poolId, String clientId, String clientSecret) {
        this.cognitoClient = cognitoClient;
        this.poolId = poolId;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public AdminInitiateAuthResponse adminInitiateAuth(String username, String password) {
        Map<String,String> authParameters = new HashMap<>();
        authParameters.put("USERNAME", username);
        authParameters.put("PASSWORD", password);

        if (StringUtils.isNotBlank(this.clientSecret)) {
            String secretHash = HashUtils.computeSecretHash(this.clientId, this.clientSecret, username);
            authParameters.put("SECRET_HASH", secretHash);
        }

        AdminInitiateAuthRequest authRequest = AdminInitiateAuthRequest.builder()
                .userPoolId(poolId)
                .clientId(clientId)
                .authFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                .authParameters(authParameters)
                .build();

        try {
            AdminInitiateAuthResponse response = this.cognitoClient.adminInitiateAuth(authRequest);
            return response;
        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }

    public AdminRespondToAuthChallengeResponse adminRespondToAuthChallenge(String session, String username) {
        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", username);

        if (StringUtils.isNotBlank(this.clientSecret)) {
            String secretHash = HashUtils.computeSecretHash(this.clientId, this.clientSecret, username);
            challengeResponses.put("SECRET_HASH", secretHash);
        }

        AdminRespondToAuthChallengeRequest challengeRequest = AdminRespondToAuthChallengeRequest.builder()
                .userPoolId(poolId)
                .clientId(clientId)
                .session(session)
                .challengeName(ChallengeNameType.MFA_SETUP)
                .challengeResponses(challengeResponses)
                .build();

        try {
            AdminRespondToAuthChallengeResponse response = this.cognitoClient.adminRespondToAuthChallenge(challengeRequest);
            return response;
        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
}
