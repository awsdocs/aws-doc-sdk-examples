//snippet-sourcedescription:[Demonstrates how to abort a multipart upload to an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[AWS Lambda]
//snippet-sourcetype:[full-example]
// snippet-sourcedate:[04/14/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lambda

// snippet-start:[lambda.kotlin.account.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.GetAccountSettingsRequest
import aws.sdk.kotlin.services.lambda.model.GetAccountSettingsResponse
import aws.sdk.kotlin.services.lambda.model.LambdaException
import kotlin.system.exitProcess
// snippet-end:[lambda.kotlin.account.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    println("About to create a LambaClient")
    val lambdaClient = LambdaClient { region = "us-east-1" }
    getSettings(lambdaClient)
    lambdaClient.close()

}

// snippet-start:[lambda.kotlin.account.main]
    suspend fun getSettings(awsLambda: LambdaClient) {
        try {

            val response :GetAccountSettingsResponse = awsLambda.getAccountSettings(GetAccountSettingsRequest{ })
            println( "Total code size for your account is ${response.accountLimit?.totalCodeSize.toString()} bytes"
            )

        } catch (ex: LambdaException) {
            println(ex.message)
            awsLambda.close()
            exitProcess(1)
        }
 }
// snippet-end:[lambda.kotlin.account.main]