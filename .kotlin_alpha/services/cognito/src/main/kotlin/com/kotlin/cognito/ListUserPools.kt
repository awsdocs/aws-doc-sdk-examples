//snippet-sourcedescription:[ListUserPools.kt demonstrates how to to list existing user pools in the given account.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Cognito]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[08/01/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.cognito

//snippet-start:[cognito.kotlin.ListUserPools.import]
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ListUserPoolsRequest
import aws.sdk.kotlin.services.cognitoidentityprovider.model.UserPoolDescriptionType
import aws.sdk.kotlin.services.cognitoidentity.model.CognitoIdentityException
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.ListUserPools.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main(){

    val cognitoClient = CognitoIdentityProviderClient { region = "us-east-1" }
    getPools(cognitoClient)
    cognitoClient.close()
}

//snippet-start:[cognito.kotlin.ListUserPools.main]
suspend fun getPools(cognitoClient:CognitoIdentityProviderClient) {

        try {
            val  listUserPoolsRequest =  ListUserPoolsRequest{
                maxResults = 10
            }

            val response = cognitoClient.listUserPools(listUserPoolsRequest)
            val pools = response.userPools

            if (pools != null) {

                for (pool: UserPoolDescriptionType  in pools) {
                    println("The user pool name is ${pool.name}")
                }
        }

        } catch (ex: CognitoIdentityException) {
            println(ex.message)
            cognitoClient.close()
            exitProcess(0)
        }
   }
//snippet-end:[cognito.kotlin.ListUserPools.main]