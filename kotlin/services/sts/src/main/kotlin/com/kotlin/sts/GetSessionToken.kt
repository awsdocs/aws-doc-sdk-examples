// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetSessionToken.kt demonstrates how to return a set of temporary credentials.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Security Token Service (AWS STS)]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/05/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sts

// snippet-start:[sts.kotlin.get_session_token.import]
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.model.GetSessionTokenRequest
// snippet-end:[sts.kotlin.get_session_token.import]


/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.
For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    getToken()
}

// snippet-start:[sts.kotlin.get_session_token.main]
suspend fun getToken() {

    val request = GetSessionTokenRequest{
        durationSeconds = 1500
    }

    StsClient { region = "us-east-1" }.use { stsClient ->
        val tokenResponse = stsClient.getSessionToken(request)
        println("The token value is ${tokenResponse.credentials?.sessionToken}")
    }
}
// snippet-end:[sts.kotlin.get_session_token.main]