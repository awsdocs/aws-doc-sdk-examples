//snippet-sourcedescription:[CognitoMVP.java demonstrates how to sign up a new user with Amazon Cognito and associate the user with an MFA application for multi-factor authentication.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[07/19/2022]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.cognito;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AssociateSoftwareTokenResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ChallengeNameType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ConfirmSignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResendConfirmationCodeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ResendConfirmationCodeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.RespondToAuthChallengeResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.SignUpRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.VerifySoftwareTokenResponse;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 * This code example performs these operations:
 *
 * 1. Sign up a user.
 * 2. AdminGetUser to get user confirmation status if user exists.
 * 3. ResendConfirmationCode if user needs another code.
 * 4. Confirm signup: ConfirmSignUp
 * 5. Sign in, get prompted to set up TOTP (Time-based one-time password) MFA: AdminInitiateAuth with ADMIN_USER_PASSWORD_AUTH (Response: “ChallengeName”: “MFA_SETUP”)
 * 6. Generate a TOTP MFA private key: AssociateSoftwareToken.
 * 7. Verify the TOTP and register for MFA: VerifySoftwareToken
 * 8. Sign in again, get prompted to submit TOTP: AdminInitiateAuth with ADMIN_USER_PASSWORD_AUTH (Response: “ChallengeName”: “SOFTWARE_TOKEN_MFA”)
 * 9. Provide TOTP, get tokens: AdminRespondToAuthChallenge
 *
 * NOTE: You can set up the required User Pool by running the AWS CDK script provided in this Github repo at resources/cdk/cognito_scenario_user_pool_with_mfa.
 */

public class CognitoMVP {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        final String usage = "\n" +
            "Usage:\n" +
            "    <clientId> <poolId>\n\n" +
            "Where:\n" +
            "    clientId - The app client id value that you can obtain from the AWS CDK script.\n\n" +
            "    poolId - The Id of the pool you can obtain from the AWS CDK script. \n\n" ;

         //  if (args.length != 2) {
         //          System.out.println(usage);
         //          System.exit(1);
         //  }

        String clientId = "65kshga9h31bhb9t5d9stlffm";  //args[0];
        String poolId = "us-east-1_pExGe6XAk"; // args[1];

        CognitoIdentityProviderClient identityProviderClient = CognitoIdentityProviderClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        // Using Console to input data from user.
        System.out.println("*** Please enter use name");
        Scanner in = new Scanner(System.in);
        String userName = in.nextLine();

        System.out.println("*** Please enter password");
        String password = in.nextLine();

        System.out.println("*** Please enter email");
        String email = in.nextLine();

        System.out.println("*** Signing up " + userName);
        signUp(identityProviderClient, clientId, userName, password, email);

        System.out.println("*** Getting " + userName + " in the user pool");
        getAdminUser(identityProviderClient, userName, poolId);

        System.out.println("*** Conformation code sent to " + userName + ". Would you like to send a new code? (Yes/No)");
        String ans = in.nextLine();

        if (ans.compareTo("Yes") == 0) {
            resendConfirmationCode(identityProviderClient, clientId, userName);
            System.out.println("*** Sending a new confirmation code");
        }

        System.out.println("*** Please enter confirmation code ");
        String code = in.nextLine();
        confirmSignUp(identityProviderClient, clientId, code, userName);

        System.out.println("*** Rechecking the status " + userName + " in the user pool");
        getAdminUser(identityProviderClient, userName, poolId);

        InitiateAuthResponse authResponse = initiateAuth(identityProviderClient, clientId, userName, password) ;
        String mySession = authResponse.session() ;
        String newSession = getSecretForAppMFA(identityProviderClient, mySession);

        System.out.println("*** Please enter 6 digit code displayed in Google Authenticator");
        String myCode = in.nextLine();

        // Verify the TOTP and register for MFA.
        verifyTOTP(identityProviderClient, newSession, myCode);
        System.out.println("*** Re-enter a 6 digit code displayed in Google Authenticator");
        String mfaCode = in.nextLine();
        InitiateAuthResponse authResponse1 = initiateAuth(identityProviderClient, clientId, userName, password);
        String session2 = authResponse1.session();
        adminRespondToAuthChallenge(identityProviderClient, userName, clientId, mfaCode, session2);
    }

    // Responds to an authentication challenge.
    public static void adminRespondToAuthChallenge(CognitoIdentityProviderClient identityProviderClient, String userName, String clientId, String mfaCode, String session) {

        System.out.println("SOFTWARE_TOKEN_MFA challenge is generated");
        Map<String, String> challengeResponses = new HashMap<>();

        challengeResponses.put("USERNAME", userName);
        challengeResponses.put("SOFTWARE_TOKEN_MFA_CODE", mfaCode);

        RespondToAuthChallengeRequest respondToAuthChallengeRequest = RespondToAuthChallengeRequest.builder()
            .challengeName(ChallengeNameType.SOFTWARE_TOKEN_MFA)
            .clientId(clientId)
            .challengeResponses(challengeResponses)
            .session(session)
            .build();

        RespondToAuthChallengeResponse respondToAuthChallengeResult = identityProviderClient.respondToAuthChallenge(respondToAuthChallengeRequest);
        System.out.println("respondToAuthChallengeResult.getAuthenticationResult()" + respondToAuthChallengeResult.authenticationResult());
    }

    // Verify the TOTP and register for MFA.
    public static void verifyTOTP(CognitoIdentityProviderClient identityProviderClient, String session, String code) {

        try {
            VerifySoftwareTokenRequest tokenRequest = VerifySoftwareTokenRequest.builder()
                .userCode(code)
                .session(session)
                .build();

            VerifySoftwareTokenResponse verifyResponse = identityProviderClient.verifySoftwareToken(tokenRequest);
            System.out.println("The status of the token is " +verifyResponse.statusAsString());

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static InitiateAuthResponse initiateAuth(CognitoIdentityProviderClient identityProviderClient, String clientId, String userName, String password) {

        try {
            Map<String,String> authParameters = new HashMap<>();
            authParameters.put("USERNAME", userName);
            authParameters.put("PASSWORD", password);

            InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .clientId(clientId)
                .authParameters(authParameters)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .build();

            InitiateAuthResponse response = identityProviderClient.initiateAuth(authRequest);
            System.out.println("Result Challenge is : " + response.challengeName() );
            return response;

        } catch(CognitoIdentityProviderException e) {
               System.err.println(e.awsErrorDetails().errorMessage());
               System.exit(1);
        }

        return null;
    }

    public static String getSecretForAppMFA(CognitoIdentityProviderClient identityProviderClient, String session) {

        AssociateSoftwareTokenRequest softwareTokenRequest = AssociateSoftwareTokenRequest.builder()
            .session(session)
            .build();

        AssociateSoftwareTokenResponse tokenResponse = identityProviderClient.associateSoftwareToken(softwareTokenRequest) ;
        String secretCode = tokenResponse.secretCode();
        System.out.println("Enter this token into Google Authenticator");
        System.out.println(secretCode);
        return tokenResponse.session();
    }

    public static void confirmSignUp(CognitoIdentityProviderClient identityProviderClient, String clientId, String code, String userName) {

        try {
            ConfirmSignUpRequest signUpRequest = ConfirmSignUpRequest.builder()
                .clientId(clientId)
                .confirmationCode(code)
                .username(userName)
                .build();

            identityProviderClient.confirmSignUp(signUpRequest);
            System.out.println(userName +" was confirmed");

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void resendConfirmationCode(CognitoIdentityProviderClient identityProviderClient, String clientId, String userName) {

        try {
            ResendConfirmationCodeRequest codeRequest = ResendConfirmationCodeRequest.builder()
                .clientId(clientId)
                .username(userName)
                .build();

            ResendConfirmationCodeResponse response = identityProviderClient.resendConfirmationCode(codeRequest);
            System.out.println("Method of delivery is "+response.codeDeliveryDetails().deliveryMediumAsString());

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void signUp(CognitoIdentityProviderClient identityProviderClient, String clientId, String userName, String password, String email) {

        AttributeType userAttrs = AttributeType.builder()
            .name("email")
            .value(email)
            .build();

        List<AttributeType> userAttrsList = new ArrayList<>();
        userAttrsList.add(userAttrs);

        try {
            SignUpRequest signUpRequest = SignUpRequest.builder()
                .userAttributes(userAttrsList)
                .username(userName)
                .clientId(clientId)
                .password(password)
                .build();

            identityProviderClient.signUp(signUpRequest);
            System.out.println("User has been signed up ");

        } catch(CognitoIdentityProviderException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }

    public static void getAdminUser(CognitoIdentityProviderClient identityProviderClient, String userName, String poolId) {

        try {
            AdminGetUserRequest userRequest = AdminGetUserRequest.builder()
                .username(userName)
                .userPoolId(poolId)
                .build();

            AdminGetUserResponse response = identityProviderClient.adminGetUser(userRequest);
            System.out.println("User status "+response.userStatusAsString());

        } catch (CognitoIdentityProviderException e){
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
