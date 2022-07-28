// snippet-sourcedescription:[ListIdentityPools.kt demonstrates how to list Amazon Cognito identity pools.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Cognito]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

// snippet-start:[cognito.kotlin.listproviders.import]
import aws.sdk.kotlin.services.cognitoidentity.CognitoIdentityClient
import aws.sdk.kotlin.services.cognitoidentity.model.ListIdentityPoolsRequest
// snippet-end:[cognito.kotlin.listproviders.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    getPools()
}

// snippet-start:[cognito.kotlin.listproviders.main]
suspend fun getPools() {

    val request = ListIdentityPoolsRequest {
        maxResults = 10
    }

    CognitoIdentityClient { region = "us-east-1" }.use { cognitoIdentityClient ->
        val response = cognitoIdentityClient.listIdentityPools(request)
        response.identityPools?.forEach { pool ->
            println("The identity pool name is ${pool.identityPoolName}")
        }
    }
}
// snippet-end:[cognito.kotlin.listproviders.main]
