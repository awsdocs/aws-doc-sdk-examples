// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.kotlin.cognito

// snippet-start:[cognito.kotlin.new_admin_user.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AdminCreateUserRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.AttributeType
import kotlin.system.exitProcess
// snippet-end:[cognito.kotlin.new_admin_user.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(args: Array<String>) {
    val usage = """
    Usage:
        <userPoolId> <userName> <email> <password>

    Where:
        userPoolId - The Id value for the user pool where the user will be created.
        userName - The user name.
        email - The email to use for verifying the account.
        password - The password to use (the characters that are allowed are uppercase, lowercase letters, numbers and at least one special character).
    """

    if (args.size != 4) {
        println(usage)
        exitProcess(0)
    }

    val userPoolId = args[0]
    val userName = args[1]
    val email = args[2]
    val password = args[3]
    createNewUser(userPoolId, userName, email, password)
}

// snippet-start:[cognito.kotlin.new_admin_user.main]
suspend fun createNewUser(
    userPoolId: String,
    name: String,
    email: String,
    password: String,
) {
    val attType =
        AttributeType {
            this.name = "email"
            value = email
        }

    val request =
        AdminCreateUserRequest {
            this.userPoolId = userPoolId
            username = name
            temporaryPassword = password
            userAttributes = listOf(attType)
        }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
        val response = cognitoClient.adminCreateUser(request)
        println("User ${response.user?.username} is created. Status is ${response.user?.userStatus}")
    }
}
// snippet-end:[cognito.kotlin.new_admin_user.main]
