//snippet-sourcedescription:[CreateUser.java demonstrates how to add a new user to your user pool.]
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

//snippet-start:[cognito.kotlin.new_admin_user.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminCreateUserRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import aws.sdk.kotlin.services.cognitoidentity.model.CognitoIdentityException
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.new_admin_user.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>){

    val usage = """
    Usage:
        <userPoolId> <userName> <email> <password>

    Where:
        userPoolId - the Id value for the user pool where the user will be created.
        userName - the user name for the admin user.
        email - the email to use for verifying the admin account.
        password - the password to use (the characters that are allowed are uppercase, lowercase letters, numbers and at least one special character).
    """

     if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val userPoolId = args[0]
    val userName = args[1]
    val email = args[2]
    val password = args[3]

    val cognitoClient = CognitoIdentityProviderClient { region = "us-east-1" }
    createNewUser(cognitoClient, userPoolId, userName, email, password)
    cognitoClient.close()
}

//snippet-start:[cognito.kotlin.new_admin_user.main]
suspend fun createNewUser(
    cognitoClient: CognitoIdentityProviderClient,
        userPoolId: String?,
        name: String?,
        email: String?,
        password : String?
    ) {

        try {

            val attType = AttributeType{
                this.name = "email"
                value = email
            }

            val adminCreateUserRequest = AdminCreateUserRequest{
                this.userPoolId = userPoolId
                username = name!!
                temporaryPassword = password
                userAttributes = listOf(attType)
            }

            val response = cognitoClient.adminCreateUser(adminCreateUserRequest)
            println("User ${response.user?.username.toString()} is created. Status is ${response.user?.userStatus}")

        } catch (ex: CognitoIdentityException) {
            println(ex.message)
            cognitoClient.close()
            exitProcess(0)
        }
    }
//snippet-end:[cognito.kotlin.new_admin_user.main]