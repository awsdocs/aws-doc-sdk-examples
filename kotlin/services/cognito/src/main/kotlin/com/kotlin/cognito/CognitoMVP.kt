// snippet-sourcedescription:[CognitoMVP.kt demonstrates how to sign up a new user with Amazon Cognito and associate the user with an MFA application for multi-factor authentication.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[Code Sample]
// snippet-service:[Amazon Cognito]
// snippet-sourcetype:[full-example]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

// snippet-start:[cognito.kotlin.mvp.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminGetUserRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AssociateSoftwareTokenRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AuthFlowType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ChallengeNameType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ConfirmSignUpRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.InitiateAuthResponse
import aws.sdk.kotlin.services.cognitoidentityprovider.model.RespondToAuthChallengeRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.SignUpRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.VerifySoftwareTokenRequest
import java.util.Scanner
// snippet-end:[cognito.kotlin.mvp.import]

/**
 Before running this Kotlin code example, set up your development environment, including your credentials.

 For more information, see the following documentation:
 https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html

 TIP: To set up the required user pool, run the AWS CDK script provided in this GitHub repo at resources/cdk/cognito_scenario_user_pool_with_mfa.

 This code example performs the following operations:

 1. Invokes the signUp method to sign up a user.
 2. Invokes the adminGetUser method to get the user's confirmation status.
 3. Invokes the ResendConfirmationCode method if the user requested another code.
 4. Invokes the confirmSignUp method.
 5. Invokes the initiateAuth to sign in. This results in being prompted to set up TOTP (time-based one-time password). (The response is “ChallengeName”: “MFA_SETUP”).
 6. Invokes the AssociateSoftwareToken method to generate a TOTP MFA private key. This can be used with Google Authenticator.
 7. Invokes the VerifySoftwareToken method to verify the TOTP and register for MFA.
 8. Invokes the AdminInitiateAuth to sign in again. This results in being prompted to submit a TOTP (Response: “ChallengeName”: “SOFTWARE_TOKEN_MFA”).
 9. Invokes the AdminRespondToAuthChallenge to get back a token.
 */

// snippet-start:[cognito.kotlin.mvp.main]
suspend fun main(args: Array<String>) {

    val usage = """
        Usage:
            <clientId> <poolId>
        Where:
            clientId - The app client Id value that you can get from the AWS CDK script.
            poolId - The pool Id that you can get from the AWS CDK script. 
    """

    // if (args.size != 1) {
    //     println(usage)
    //     System.exit(1)
    // }

    val clientId = "65kshga9h31bhb9t5d9stlffm" // args[0]
    val poolId = "us-east-1_pExGe6XAk" // args[0]

    // Use the console to get data from the user.
    println("*** Enter your use name")
    val inOb = Scanner(System.`in`)
    val userName = inOb.nextLine()
    println(userName)

    println("*** Enter your password")
    val password: String = inOb.nextLine()

    println("*** Enter your email")
    val email = inOb.nextLine()

    println("*** Signing up $userName")
    signUp(clientId, userName, password, email)

    println("*** Getting $userName in the user pool")
    getAdminUser(userName, poolId)

    println("*** Conformation code sent to $userName. Would you like to send a new code? (Yes/No)")
    val ans = inOb.nextLine()

    if (ans.compareTo("Yes") == 0) {
        println("*** Sending a new confirmation code")
    }
    println("*** Enter the confirmation code that was emailed")
    val code = inOb.nextLine()
    confirmSignUp(clientId, code, userName)

    println("*** Rechecking the status of $userName in the user pool")
    getAdminUser(userName, poolId)

    val authResponse = initiateAuth(clientId, userName, password)
    val mySession = authResponse.session
    val newSession = getSecretForAppMFA(mySession)
    println("*** Enter the 6-digit code displayed in Google Authenticator")
    val myCode = inOb.nextLine()

    // Verify the TOTP and register for MFA.
    verifyTOTP(newSession, myCode)
    println("*** Re-enter a 6-digit code displayed in Google Authenticator")
    val mfaCode: String = inOb.nextLine()
    val authResponse1 = initiateAuth(clientId, userName, password)
    val session2 = authResponse1.session
    adminRespondToAuthChallenge(userName, clientId, mfaCode, session2)
}

// Respond to an authentication challenge.
suspend fun adminRespondToAuthChallenge(userName: String, clientIdVal: String?, mfaCode: String, sessionVal: String?) {

    println("SOFTWARE_TOKEN_MFA challenge is generated")
    val challengeResponsesOb = mutableMapOf<String, String>()
    challengeResponsesOb["USERNAME"] = userName
    challengeResponsesOb["SOFTWARE_TOKEN_MFA_CODE"] = mfaCode

    val respondToAuthChallengeRequest = RespondToAuthChallengeRequest {
        challengeName = ChallengeNameType.SoftwareTokenMfa
        clientId = clientIdVal
        challengeResponses = challengeResponsesOb
        session = sessionVal
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        val respondToAuthChallengeResult = identityProviderClient.respondToAuthChallenge(respondToAuthChallengeRequest)
        println("respondToAuthChallengeResult.getAuthenticationResult() ${respondToAuthChallengeResult.authenticationResult}")
    }
}

// Verify the TOTP and register for MFA.
suspend fun verifyTOTP(sessionVal: String?, codeVal: String?) {

    val tokenRequest = VerifySoftwareTokenRequest {
        userCode = codeVal
        session = sessionVal
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        val verifyResponse = identityProviderClient.verifySoftwareToken(tokenRequest)
        println("The status of the token is ${verifyResponse.status}")
    }
}

suspend fun getSecretForAppMFA(sessionVal: String?): String? {

    val softwareTokenRequest = AssociateSoftwareTokenRequest {
        session = sessionVal
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        val tokenResponse = identityProviderClient.associateSoftwareToken(softwareTokenRequest)
        val secretCode = tokenResponse.secretCode
        println("Enter this token into Google Authenticator")
        println(secretCode)
        return tokenResponse.session
    }
}

suspend fun initiateAuth(clientIdVal: String?, userNameVal: String, passwordVal: String): InitiateAuthResponse {

    val authParas = mutableMapOf <String, String>()
    authParas["USERNAME"] = userNameVal
    authParas["PASSWORD"] = passwordVal

    val authRequest = InitiateAuthRequest {
        clientId = clientIdVal
        authParameters = authParas
        authFlow = AuthFlowType.UserPasswordAuth
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        val response = identityProviderClient.initiateAuth(authRequest)
        println("Result Challenge is ${response.challengeName}")
        return response
    }
}

suspend fun confirmSignUp(clientIdVal: String?, codeVal: String?, userNameVal: String?) {

    val signUpRequest = ConfirmSignUpRequest {
        clientId = clientIdVal
        confirmationCode = codeVal
        username = userNameVal
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        identityProviderClient.confirmSignUp(signUpRequest)
        println("$userNameVal  was confirmed")
    }
}

suspend fun getAdminUser(userNameVal: String?, poolIdVal: String?) {

    val userRequest = AdminGetUserRequest {
        username = userNameVal
        userPoolId = poolIdVal
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        val response = identityProviderClient.adminGetUser(userRequest)
        println("User status ${response.userStatus}")
    }
}

suspend fun signUp(clientIdVal: String?, userNameVal: String?, passwordVal: String?, emailVal: String?) {

    val userAttrs = AttributeType {
        name = "email"
        value = emailVal
    }

    val userAttrsList = mutableListOf<AttributeType>()
    userAttrsList.add(userAttrs)

    val signUpRequest = SignUpRequest {
        userAttributes = userAttrsList
        username = userNameVal
        clientId = clientIdVal
        password = passwordVal
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
        identityProviderClient.signUp(signUpRequest)
        println("User has been signed up")
    }
}
// snippet-end:[cognito.kotlin.mvp.main]
