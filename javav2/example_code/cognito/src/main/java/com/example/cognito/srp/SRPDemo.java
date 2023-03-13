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
package com.example.cognito.srp;

import com.example.cognito.srp.usecases.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Scanner;

public class SRPDemo {
    private static final String COGNITO_USERNAME = "test_user-auth-device-tracking";
    private static final String COGNITO_PASSWORD = "changeme-0okm9IJN~";
    private static final String COGNITO_POOL_ID;
    private static final String COGNITO_USER_CLIENT_ID;
    private static final String COGNITO_ADMIN_CLIENT_ID;
    private static final String COGNITO_ADMIN_CLIENT_SECRET;
    private static final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

    static {
        // Create user pool.
        DeviceConfigurationType deviceConfig = DeviceConfigurationType.builder()
                .challengeRequiredOnNewDevice(true)
                .deviceOnlyRememberedOnUserPrompt(true)
                .build();
        CreateUserPoolRequest poolRequest = CreateUserPoolRequest.builder()
                .poolName("my awesome user pool")
                .deviceConfiguration(deviceConfig)
                .build();
        CreateUserPoolResponse pool = cognitoClient.createUserPool(poolRequest);
        COGNITO_POOL_ID = pool.userPool().id();
        System.out.println("User pool is: " + COGNITO_POOL_ID);
        SetUserPoolMfaConfigRequest mfaRequest = SetUserPoolMfaConfigRequest.builder()
                .userPoolId(COGNITO_POOL_ID)
                .mfaConfiguration(UserPoolMfaType.ON)
                .softwareTokenMfaConfiguration(SoftwareTokenMfaConfigType.builder().enabled(true).build())
                .build();
        cognitoClient.setUserPoolMfaConfig(mfaRequest);
        // Create user application client.
        CreateUserPoolClientRequest userClientRequest = CreateUserPoolClientRequest.builder()
                .clientName("my awesome application client for user")
                .userPoolId(COGNITO_POOL_ID)
                .explicitAuthFlows(ExplicitAuthFlowsType.ALLOW_USER_SRP_AUTH, ExplicitAuthFlowsType.ALLOW_REFRESH_TOKEN_AUTH)
                .generateSecret(false)
                .build();
        CreateUserPoolClientResponse userClientResponse = cognitoClient.createUserPoolClient(userClientRequest);
        COGNITO_USER_CLIENT_ID = userClientResponse.userPoolClient().clientId();
        System.out.println("Application client for user is: " + COGNITO_USER_CLIENT_ID);
        // Create admin application client.
        CreateUserPoolClientRequest adminClientRequest = CreateUserPoolClientRequest.builder()
                .clientName("my awesome application client for admin")
                .userPoolId(COGNITO_POOL_ID)
                .explicitAuthFlows(ExplicitAuthFlowsType.ALLOW_ADMIN_USER_PASSWORD_AUTH, ExplicitAuthFlowsType.ALLOW_REFRESH_TOKEN_AUTH)
                .generateSecret(true)
                .build();
        CreateUserPoolClientResponse adminClientResponse = cognitoClient.createUserPoolClient(adminClientRequest);
        COGNITO_ADMIN_CLIENT_ID = adminClientResponse.userPoolClient().clientId();
        COGNITO_ADMIN_CLIENT_SECRET = adminClientResponse.userPoolClient().clientSecret();
        System.out.println("Application client for admin is: " + COGNITO_ADMIN_CLIENT_ID);
        // Create user.
        AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                .userPoolId(COGNITO_POOL_ID)
                .username(COGNITO_USERNAME)
                .build();
        cognitoClient.adminCreateUser(userRequest);
        AdminSetUserPasswordRequest passwordRequest = AdminSetUserPasswordRequest.builder()
                .userPoolId(COGNITO_POOL_ID)
                .username(COGNITO_USERNAME)
                .password(COGNITO_PASSWORD)
                .permanent(true)
                .build();
        cognitoClient.adminSetUserPassword(passwordRequest);
    }

    public static void main(String[] args) {
        // 1. Start authentication with Admin credentials.
        AdminInitiateAuthResponse authResponse = new AdminAuthDemo(cognitoClient, COGNITO_POOL_ID, COGNITO_ADMIN_CLIENT_ID, COGNITO_ADMIN_CLIENT_SECRET)
                .adminInitiateAuth(COGNITO_USERNAME, COGNITO_PASSWORD);
        System.out.println(authResponse);

        // 2. Get a token to associate an MFA application.
        String session = authResponse.session();
        AssociateSoftwareTokenResponse associateResponse = new SoftwareTokenMFADemo(cognitoClient).associateSoftwareToken(session);
        session = associateResponse.session();
        String secretCode = associateResponse.secretCode();
        System.out.println("Please enter this token into Google Authenticator: " + secretCode);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter TOTP from Google authenticator: ");
        String code = scanner.nextLine();
        VerifySoftwareTokenResponse verifyResponse = new SoftwareTokenMFADemo(cognitoClient).verifySoftwareToken(session, code);

        // 3. Respond to an auth challenge [MFA_SETUP].
        session = verifyResponse.session();
        AdminRespondToAuthChallengeResponse challengeResponse = new AdminAuthDemo(cognitoClient, COGNITO_POOL_ID, COGNITO_ADMIN_CLIENT_ID, COGNITO_ADMIN_CLIENT_SECRET)
                .adminRespondToAuthChallenge(session, COGNITO_USERNAME);
        System.out.println("Challenge response: " + challengeResponse);

        // 4. Confirm an MFA device for tracking.
        String accessToken = challengeResponse.authenticationResult().accessToken();
        String deviceGroupKey = challengeResponse.authenticationResult().newDeviceMetadata().deviceGroupKey();
        String deviceKey = challengeResponse.authenticationResult().newDeviceMetadata().deviceKey();
        String deviceName = "my cool device name";
        String devicePassword = "my secret device password";
        ConfirmDeviceResponse confirmResponse = new DeviceTrackingDemo(cognitoClient)
                .confirmDevice(accessToken, deviceGroupKey, deviceKey, deviceName, devicePassword);
        System.out.println("Confirm response: " + confirmResponse);
        if (confirmResponse.userConfirmationNecessary()) {
            UpdateDeviceStatusResponse deviceResponse = new DeviceTrackingDemo(cognitoClient)
                    .updateDeviceStatus(accessToken, deviceKey);
        }

        // 5. User SRP auth.
        RespondToAuthChallengeResponse userSrpResponse = new UserAuthDemo(cognitoClient, COGNITO_POOL_ID, COGNITO_USER_CLIENT_ID)
                .userSrpAuth(COGNITO_USERNAME, COGNITO_PASSWORD, deviceKey);
        System.out.println("User SRP response: " + userSrpResponse);

        // 6. Device SRP Auth.
        RespondToAuthChallengeResponse deviceSrpResponse = new DeviceAuthDemo(cognitoClient, COGNITO_USER_CLIENT_ID).deviceSrpAuth(COGNITO_USERNAME, deviceGroupKey, deviceKey, devicePassword);
        System.out.println("Device SRP response: " +  deviceSrpResponse);
    }
}
