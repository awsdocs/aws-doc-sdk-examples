// snippet-sourcedescription:[GetAccountSettings.kt demonstrates how to get AWS Lambda account settings.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[AWS Lambda]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.lambda

// snippet-start:[lambda.kotlin.account.import]
import aws.sdk.kotlin.services.lambda.LambdaClient
import aws.sdk.kotlin.services.lambda.model.GetAccountSettingsRequest
// snippet-end:[lambda.kotlin.account.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    println("About to create a LambaClient")
    getSettings()
}

// snippet-start:[lambda.kotlin.account.main]
suspend fun getSettings() {

    LambdaClient { region = "us-west-2" }.use { awsLambda ->
        val response = awsLambda.getAccountSettings(GetAccountSettingsRequest { })
        println(
            "Total code size for your account is ${response.accountLimit?.totalCodeSize} bytes"
        )
    }
}
// snippet-end:[lambda.kotlin.account.main]
