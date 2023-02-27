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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

import static java.security.CryptoPrimitive.SECURE_RANDOM;

public class DeviceTrackingDemo {
    private final CognitoIdentityProviderClient cognitoClient;

    public DeviceTrackingDemo(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    public ConfirmDeviceResponse confirmDevice(String accessToken, String deviceGroupKey, String deviceKey, String deviceName, String devicePassword) {
        BigInteger salt = SRPUtils.generateSalt();
        BigInteger passwordVerifier = SRPUtils.generatePasswordVerifier(deviceGroupKey + deviceKey, devicePassword, salt);

        DeviceSecretVerifierConfigType verifierConfig = DeviceSecretVerifierConfigType.builder()
                .salt(Base64.getEncoder().encodeToString(salt.toByteArray()))
                .passwordVerifier(Base64.getEncoder().encodeToString(passwordVerifier.toByteArray()))
                .build();

        ConfirmDeviceRequest confirmRequest = ConfirmDeviceRequest.builder()
                .accessToken(accessToken)
                .deviceKey(deviceKey)
                .deviceSecretVerifierConfig(verifierConfig)
                .deviceName(deviceName)
                .build();

        try {
            ConfirmDeviceResponse response = cognitoClient.confirmDevice(confirmRequest);
            return response;
        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }

    public UpdateDeviceStatusResponse updateDeviceStatus(String accessToken, String deviceKey) {
        UpdateDeviceStatusRequest deviceRequest = UpdateDeviceStatusRequest.builder()
                .accessToken(accessToken)
                .deviceKey(deviceKey)
                .deviceRememberedStatus(DeviceRememberedStatusType.REMEMBERED)
                .build();

        try {
            UpdateDeviceStatusResponse response = cognitoClient.updateDeviceStatus(deviceRequest);
            return response;
        } catch (CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return null;
    }
}
