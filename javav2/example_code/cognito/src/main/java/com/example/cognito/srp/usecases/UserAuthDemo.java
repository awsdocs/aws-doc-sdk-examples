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

import com.example.cognito.srp.utils.SRPUtils;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;
import software.amazon.awssdk.utils.Pair;

import java.math.BigInteger;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UserAuthDemo {
    private final CognitoIdentityProviderClient cognitoClient;
    private final String poolId;
    private final String clientId;

    public UserAuthDemo(CognitoIdentityProviderClient cognitoClient, String poolId, String clientId) {
        this.cognitoClient = cognitoClient;
        this.poolId = poolId;
        this.clientId = clientId;
    }

    public RespondToAuthChallengeResponse userSrpAuth(String username, String password, String deviceKey) {
        Pair<BigInteger, BigInteger> clientKeys = SRPUtils.generateSrpClientKeys();
        BigInteger a = clientKeys.left();
        BigInteger A = clientKeys.right();
        InitiateAuthResponse initiateAuthResponse = initiateAuth(username, A.toString(16));
        System.out.println("InitiateAuth response: " + initiateAuthResponse);
        // Get response from SRP initial auth.
        BigInteger B = new BigInteger(initiateAuthResponse.challengeParameters().get("SRP_B"), 16);
        BigInteger salt = new BigInteger(initiateAuthResponse.challengeParameters().get("SALT"), 16);
        String userId = initiateAuthResponse.challengeParameters().get("USER_ID_FOR_SRP");
        String secretBlock = initiateAuthResponse.challengeParameters().get("SECRET_BLOCK");

        // Calculate password claim signature.
        String timestamp = SRPUtils.getCurrentTimestamp();
        System.out.println("Current timestamp: " + timestamp);
        byte[] passwordClaimSignature = SRPUtils.calculatePasswordClaimSignature(
                poolId.split("_")[1] + userId, password, timestamp,
                a, A, B, salt,
                Base64.getDecoder().decode(secretBlock));

        RespondToAuthChallengeResponse challengeResponse = respondToAuthChallengeResponse(userId, secretBlock, timestamp,
                Base64.getEncoder().encodeToString(passwordClaimSignature), deviceKey);
        System.out.println("RespondToAuthChallenge response: " + challengeResponse);
        return challengeResponse;
    }

    public InitiateAuthResponse initiateAuth(String username, String A) {
        Map<String,String> authParameters = new HashMap<>();
        authParameters.put("USERNAME", username);
        authParameters.put("SRP_A", A);

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .clientId(clientId)
                .authFlow(AuthFlowType.USER_SRP_AUTH)
                .authParameters(authParameters)
                .build();

        try {
            InitiateAuthResponse response = this.cognitoClient.initiateAuth(authRequest);
            return response;
        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }

    public RespondToAuthChallengeResponse respondToAuthChallengeResponse(String userId, String secretBlock, String timestamp,
                                                                         String passwordClaimSignature, String deviceKey) {
        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", userId);
        challengeResponses.put("PASSWORD_CLAIM_SECRET_BLOCK", secretBlock);
        challengeResponses.put("PASSWORD_CLAIM_SIGNATURE", passwordClaimSignature);
        challengeResponses.put("TIMESTAMP", timestamp);
        challengeResponses.put("DEVICE_KEY", deviceKey);


        RespondToAuthChallengeRequest challengeRequest = RespondToAuthChallengeRequest.builder()
                .clientId(clientId)
                .challengeName(ChallengeNameType.PASSWORD_VERIFIER)
                .challengeResponses(challengeResponses)
                .build();

        try {
            RespondToAuthChallengeResponse response = this.cognitoClient.respondToAuthChallenge(challengeRequest);
            return response;
        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
}
