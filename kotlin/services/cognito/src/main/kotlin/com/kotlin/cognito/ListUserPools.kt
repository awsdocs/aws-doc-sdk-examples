// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.cognito

// snippet-start:[cognito.kotlin.ListUserPools.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ListUserPoolsRequest
// snippet-end:[cognito.kotlin.ListUserPools.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getAllPools()
}

// snippet-start:[cognito.kotlin.ListUserPools.main]
suspend fun getAllPools() {
    val request =
        ListUserPoolsRequest {
            maxResults = 10
        }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
        val response = cognitoClient.listUserPools(request)
        response.userPools?.forEach { pool ->
            println("The user pool name is ${pool.name}")
        }
    }
}
// snippet-end:[cognito.kotlin.ListUserPools.main]
