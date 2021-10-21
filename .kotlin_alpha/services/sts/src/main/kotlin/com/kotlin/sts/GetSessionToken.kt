// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetSessionToken.kt demonstrates how to return a set of temporary credentials.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-keyword:[AWS Security Token Service (AWS STS)]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06/07/2021]
// snippet-sourceauthor:[AWS - scmacdon]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.sts

// snippet-start:[sts.kotlin.get_session_token.import]
import aws.sdk.kotlin.services.sts.StsClient
import aws.sdk.kotlin.services.sts.model.GetSessionTokenRequest
import aws.sdk.kotlin.services.sts.model.StsException
import kotlin.system.exitProcess
// snippet-end:[sts.kotlin.get_session_token.import]


/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.
For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val stsClient = StsClient{region ="us-east-1"}
    getToken(stsClient)
    stsClient.close()
}

// snippet-start:[sts.kotlin.get_session_token.main]
suspend fun getToken(stsClient: StsClient) {
    try {
        val tokenRequest = GetSessionTokenRequest{
            durationSeconds = 1500
        }

        val tokenResponse = stsClient.getSessionToken(tokenRequest)
        System.out.println("The token value is ${tokenResponse.credentials?.sessionToken}")

    } catch (e: StsException) {
        println(e.message)
        stsClient.close()
        exitProcess(0)
    }
}
// snippet-end:[sts.kotlin.get_session_token.main]