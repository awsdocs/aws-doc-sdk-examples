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

public class DeviceAuthDemo {
    private final CognitoIdentityProviderClient cognitoClient;
    private final String clientId;

    public DeviceAuthDemo(CognitoIdentityProviderClient cognitoClient, String clientId) {
        this.cognitoClient = cognitoClient;
        this.clientId = clientId;
    }

    public RespondToAuthChallengeResponse deviceSrpAuth(String username, String deviceGroupKey, String deviceKey, String devicePassword) {
        Pair<BigInteger, BigInteger> clientKeys = SRPUtils.generateSrpClientKeys();
        BigInteger a = clientKeys.left();
        BigInteger A = clientKeys.right();
        RespondToAuthChallengeResponse deviceSrpChallengeResponse = respondToDeviceSrpAuthChallenge(username, deviceKey, A.toString(16));
        System.out.println("DeviceSrpAuth response: " + deviceSrpChallengeResponse);
        // Get response from SRP initial auth.
        BigInteger B = new BigInteger(deviceSrpChallengeResponse.challengeParameters().get("SRP_B"), 16);
        BigInteger salt = new BigInteger(deviceSrpChallengeResponse.challengeParameters().get("SALT"), 16);
        String secretBlock = deviceSrpChallengeResponse.challengeParameters().get("SECRET_BLOCK");

        // Calculate password claim signature.
        String timestamp = SRPUtils.getCurrentTimestamp();
        System.out.println("Current timestamp: " + timestamp);
        byte[] passwordClaimSignature = SRPUtils.calculatePasswordClaimSignature(
                deviceGroupKey+deviceKey, devicePassword, timestamp,
                a, A, B, salt,
                Base64.getDecoder().decode(secretBlock));

        RespondToAuthChallengeResponse challengeResponse = respondToAuthChallenge(username, secretBlock, timestamp,
                Base64.getEncoder().encodeToString(passwordClaimSignature), deviceKey);
        System.out.println("RespondToAuthChallenge response: " + challengeResponse);
        return challengeResponse;
    }

    public RespondToAuthChallengeResponse respondToDeviceSrpAuthChallenge(String username, String deviceKey, String A) {
        Map<String,String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", username);
        challengeResponses.put("DEVICE_KEY", deviceKey);
        challengeResponses.put("SRP_A", A);

        RespondToAuthChallengeRequest challengeRequest = RespondToAuthChallengeRequest.builder()
                .clientId(clientId)
                .challengeName(ChallengeNameType.DEVICE_SRP_AUTH)
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

    public RespondToAuthChallengeResponse respondToAuthChallenge(String username, String secretBlock, String timestamp,
                                                                                   String passwordClaimSignature, String deviceKey) {
        Map<String, String> challengeResponses = new HashMap<>();
        challengeResponses.put("USERNAME", username);
        challengeResponses.put("PASSWORD_CLAIM_SECRET_BLOCK", secretBlock);
        challengeResponses.put("PASSWORD_CLAIM_SIGNATURE", passwordClaimSignature);
        challengeResponses.put("TIMESTAMP", timestamp);
        challengeResponses.put("DEVICE_KEY", deviceKey);


        RespondToAuthChallengeRequest challengeRequest = RespondToAuthChallengeRequest.builder()
                .clientId(clientId)
                .challengeName(ChallengeNameType.DEVICE_PASSWORD_VERIFIER)
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
