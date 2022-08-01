// snippet-sourcedescription:[DescribeVoices.kt produces a list of all voices available for use when requesting speech synthesis with Amazon Polly.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Polly]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.polly

// snippet-start:[polly.kotlin.describe_voice.import]
import aws.sdk.kotlin.services.polly.PollyClient
import aws.sdk.kotlin.services.polly.model.DescribeVoicesRequest
import aws.sdk.kotlin.services.polly.model.LanguageCode
// snippet-end:[polly.kotlin.describe_voice.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {
    describeVoice()
}

// snippet-start:[polly.kotlin.describe_voice.main]
suspend fun describeVoice() {

    PollyClient { region = "us-west-2" }.use { polly ->
        val enUsVoicesResult = polly.describeVoices(
            DescribeVoicesRequest {
                languageCode = LanguageCode.fromValue("en-US")
            }
        )
        val voices = enUsVoicesResult.voices
        if (voices != null) {
            for (voice in voices) {
                println("The ID of the voice is ${voice.id}")
                println("The gender of the voice is ${voice.gender}")
            }
        }
    }
}
// snippet-end:[polly.kotlin.describe_voice.main]
