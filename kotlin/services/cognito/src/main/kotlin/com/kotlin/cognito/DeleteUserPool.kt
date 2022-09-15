// snippet-sourcedescription:[DeleteUserPool.kt demonstrates how to delete an existing user pool.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Cognito]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

// snippet-start:[cognito.kotlin.DeleteUserPool.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.DeleteUserPoolRequest
import kotlin.system.exitProcess
// snippet-end:[cognito.kotlin.DeleteUserPool.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(args: Array<String>) {

    val usage = """
        Usage: <userPoolId>
    
        Where:
            userPoolId - The Id value of your user pool.
        """

    if (args.size != 1) {
        println(usage)
        exitProcess(0)
    }

    val userPoolId = args[0]
    delPool(userPoolId)
}

// snippet-start:[cognito.kotlin.DeleteUserPool.main]
suspend fun delPool(userPoolId: String) {

    val request = DeleteUserPoolRequest {
        this.userPoolId = userPoolId
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
        cognitoClient.deleteUserPool(request)
        print("$userPoolId was successfully deleted")
    }
}
// snippet-end:[cognito.kotlin.DeleteUserPool.main]
