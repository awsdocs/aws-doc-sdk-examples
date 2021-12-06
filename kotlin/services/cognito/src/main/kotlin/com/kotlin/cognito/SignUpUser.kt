//snippet-sourcedescription:[SignUpUser.kt demonstrates how to register a user in the specified Amazon Cognito user pool.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/03/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

//snippet-start:[cognito.kotlin.signup.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import aws.sdk.kotlin.services.cognitoidentityprovider.model.SignUpRequest
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.signup.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
       Usage:
            <clientId> <secretkey> <userName> <password> <email>
    
       Where:
            clientId - the app client id value that you can obtain from the AWS Management Console.
            secretkey - the app client secret value that you can obtain from the AWS Management Console.
            userName - the user name of the user you wish to register.
            password - the password for the user.
            email - the email address for the user.
        """

    if (args.size != 5) {
        println(usage)
        exitProcess(0)
    }

    val clientId: String = args[0]
    val secretKey: String = args[1]
    val userName: String = args[2]
    val password: String = args[3]
    val email: String = args[4]
    signUp(clientId, secretKey, userName, password, email)
 }

//snippet-start:[cognito.kotlin.signup.main]
suspend fun signUp(clientIdVal: String, secretKey: String, userName: String, passwordVal: String, email: String) {

       val attributeType =  AttributeType {
            this.name = "email"
            this.value = email
        }

        val attrs = mutableListOf<AttributeType>()
        attrs.add(attributeType)
        val secretVal = calculateSecretHash(clientIdVal, secretKey, userName)

       val request = SignUpRequest {
          userAttributes= attrs
          username = userName
          clientId = clientIdVal
          password = passwordVal
          secretHash=secretVal
       }
       CognitoIdentityProviderClient { region = "us-east-1" }.use { identityProviderClient ->
            identityProviderClient.signUp(request)
            println("User has been signed up")
        }
    }

    fun calculateSecretHash(userPoolClientId: String, userPoolClientSecret: String, userName: String): String {
        val macSha256Algorithm = "HmacSHA256"
        val signingKey = SecretKeySpec(
            userPoolClientSecret.toByteArray(StandardCharsets.UTF_8),
            macSha256Algorithm
        )
        try {
            val mac = Mac.getInstance(macSha256Algorithm)
            mac.init(signingKey)
            mac.update(userName.toByteArray(StandardCharsets.UTF_8))
            val rawHmac = mac.doFinal(userPoolClientId.toByteArray(StandardCharsets.UTF_8))
            return Base64.getEncoder().encodeToString(rawHmac)

        }  catch(e: UnsupportedEncodingException){
            println(e.message)
        }
        return ""
   }
//snippet-end:[cognito.kotlin.signup.main]