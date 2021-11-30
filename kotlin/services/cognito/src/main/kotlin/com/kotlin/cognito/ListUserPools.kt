//snippet-sourcedescription:[ListUserPools.kt demonstrates how to to list existing user pools in the given account.]
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

//snippet-start:[cognito.kotlin.ListUserPools.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ListUserPoolsRequest
//snippet-end:[cognito.kotlin.ListUserPools.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(){
     getAllPools()
}

//snippet-start:[cognito.kotlin.ListUserPools.main]
suspend fun getAllPools() {

    val request = ListUserPoolsRequest{
        maxResults = 10
    }

    CognitoIdentityProviderClient { region = "us-east-1" }.use { cognitoClient ->
            val response = cognitoClient.listUserPools(request)
            response.userPools?.forEach { pool ->
                println("The user pool name is ${pool.name}")
            }
        }
   }
//snippet-end:[cognito.kotlin.ListUserPools.main]