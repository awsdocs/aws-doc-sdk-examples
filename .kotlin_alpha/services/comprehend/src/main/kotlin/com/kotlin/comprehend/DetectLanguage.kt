// snippet-sourcedescription:[DetectLanguage.kt demonstrates how to detect the language of the text.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Comprehend]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[03/04/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.comprehend

//snippet-start:[comprehend.kotlin.detect_language.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectDominantLanguageRequest
import aws.sdk.kotlin.services.comprehend.model.ComprehendException
import kotlin.system.exitProcess
//snippet-end:[comprehend.kotlin.detect_language.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val comprehendClient = ComprehendClient{
        region="us-east-1"
    }
    val text = "Il pleut aujourd'hui à Seattle"
    detectTheDominantLanguage(comprehendClient,text)
    comprehendClient.close()
}

//snippet-start:[comprehend.kotlin.detect_language.main]
suspend fun detectTheDominantLanguage(comClient: ComprehendClient, textVal: String?) {
        try {
            val request = DetectDominantLanguageRequest {
                text = textVal
            }

            val resp = comClient.detectDominantLanguage(request)
            val allLanList = resp.languages
            if (allLanList != null) {
                for (lang in allLanList) {
                    println("Language is ${lang.languageCode}")
                }
            }

        } catch (ex: ComprehendException) {
            println(ex.message)
            comClient.close()
            exitProcess(0)
        }
}
//snippet-end:[comprehend.kotlin.detect_language.main]
