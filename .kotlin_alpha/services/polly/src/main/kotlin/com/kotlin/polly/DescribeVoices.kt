// snippet-sourcedescription:[DescribeVoices.kt produces a list of all voices available for use when requesting speech synthesis with Amazon Polly.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Polly]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[06/02/2021]
// snippet-sourceauthor:[scmacdon AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/


package com.kotlin.polly

// snippet-start:[polly.kotlin.describe_voice.import]
import aws.sdk.kotlin.services.polly.PollyClient
import aws.sdk.kotlin.services.polly.model.DescribeVoicesRequest
import aws.sdk.kotlin.services.polly.model.LanguageCode
import aws.sdk.kotlin.services.polly.model.PollyException
import kotlin.system.exitProcess
// snippet-end:[polly.kotlin.describe_voice.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */

suspend fun main() {

    val polly = PollyClient{ region = "us-east-1" }
    describeVoice(polly)
    polly.close()
}

// snippet-start:[polly.kotlin.describe_voice.main]
suspend fun describeVoice(polly: PollyClient) {
        try {

            val voicesRequest = DescribeVoicesRequest{
                languageCode = LanguageCode.fromValue("en-US")
            }

            val enUsVoicesResult = polly.describeVoices(voicesRequest)
            val voices = enUsVoicesResult.voices
            if (voices != null) {
                for (voice in voices) {
                    println("The ID of the voice is ${voice.id}")
                    println("The gender of the voice is ${voice.gender}")
                }
            }

        } catch (ex: PollyException) {
            println(ex.message)
            polly.close()
            exitProcess(0)
        }
 }
// snippet-end:[polly.kotlin.describe_voice.main]