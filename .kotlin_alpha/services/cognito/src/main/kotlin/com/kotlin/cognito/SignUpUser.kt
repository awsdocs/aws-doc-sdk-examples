//snippet-sourcedescription:[SignUpUser.java demonstrates how to register a user in the specified Amazon Cognito user pool.]
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
import aws.sdk.kotlin.services.cognitoidentity.model.CognitoIdentityException
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

    val clientId: String = args.get(0)
    val secretKey: String = args.get(1)
    val userName: String = args.get(2)
    val password: String = args.get(3)
    val email: String = args.get(4)

    val identityProviderClient = CognitoIdentityProviderClient { region = "us-east-1" }
    signUp(identityProviderClient, clientId, secretKey, userName, password, email)
    identityProviderClient.close()
}

//snippet-start:[cognito.kotlin.signup.main]
suspend fun signUp( identityProviderClient: CognitoIdentityProviderClient, clientId: String, secretKey: String, userName: String, password: String?, email: String?) {

     val attributeType =  AttributeType() {
            this.name = "email"
            this.value = email
        }

        val attrs: MutableList<AttributeType> = ArrayList<AttributeType>()
        attrs.add(attributeType)

        try {
            val secretVal = calculateSecretHash(clientId, secretKey, userName)
            val signUpRequest = SignUpRequest {
                userAttributes= attrs
                username = userName
                this.clientId = clientId
                this.password = password
                this.secretHash=secretVal
            }

            identityProviderClient.signUp(signUpRequest)
            println("User has been signed up")

        } catch (ex: CognitoIdentityException) {
            println(ex.message)
            identityProviderClient.close()
            exitProcess(0)
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