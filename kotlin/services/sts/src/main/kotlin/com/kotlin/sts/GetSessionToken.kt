// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.kotlin.sts

// snippet-start:[sts.kotlin.get_session_token.import]
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.model.GetSessionTokenRequest
// snippet-end:[sts.kotlin.get_session_token.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getToken()
}

// snippet-start:[sts.kotlin.get_session_token.main]
suspend fun getToken() {
    val request =
        GetSessionTokenRequest {
            durationSeconds = 1500
        }

    StsClient { region = "us-east-1" }.use { stsClient ->
        val tokenResponse = stsClient.getSessionToken(request)
        println("The token value is ${tokenResponse.credentials?.sessionToken}")
    }
}
// snippet-end:[sts.kotlin.get_session_token.main]
