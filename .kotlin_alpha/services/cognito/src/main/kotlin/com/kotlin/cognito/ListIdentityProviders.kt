//snippet-sourcedescription:[ListIdentityProviders.kt demonstrates how to list Amazon Cognito identity providers.]
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

//snippet-start:[cognito.kotlin.listproviders.import]
import aws.sdk.kotlin.services.cognitoidentity.CognitoIdentityClient
import aws.sdk.kotlin.services.cognitoidentity.model.ListIdentityPoolsRequest
import aws.sdk.kotlin.services.cognitoidentity.model.CognitoIdentityException
import kotlin.system.exitProcess
//snippet-end:[cognito.kotlin.listproviders.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main(){

    val cognitoIdentityClient = CognitoIdentityClient { region = "us-east-1" }
    getPools(cognitoIdentityClient)
    cognitoIdentityClient.close()
}

//snippet-start:[cognito.kotlin.listproviders.main]
suspend fun getPools(cognitoIdentityClient:CognitoIdentityClient) {

        try {

            val listIdentityPoolsInput = ListIdentityPoolsRequest {
                maxResults = 10
            }

            val response = cognitoIdentityClient.listIdentityPools(listIdentityPoolsInput)
            val pools = response.identityPools
            if (pools != null) {

                for (pool in pools) {
                    println("The identity pool name is ${pool.identityPoolName}")
                }
            }

        } catch (ex: CognitoIdentityException) {
            println(ex.message)
            cognitoIdentityClient.close()
            exitProcess(0)
        }
  }
//snippet-end:[cognito.kotlin.listproviders.main]