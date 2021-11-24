// snippet-sourcedescription:[DetectLanguage.kt demonstrates how to detect the language of the text.]
//snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Comprehend]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[11/04/2021]
// snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.comprehend

//snippet-start:[comprehend.kotlin.detect_language.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectDominantLanguageRequest
//snippet-end:[comprehend.kotlin.detect_language.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val text = "Il pleut aujourd'hui à Seattle"
    detectTheDominantLanguage(text)
}

//snippet-start:[comprehend.kotlin.detect_language.main]
suspend fun detectTheDominantLanguage(textVal: String) {

    val request =  DetectDominantLanguageRequest {
        text = textVal
    }
    ComprehendClient { region = "us-east-1" }.use { comClient ->
            val response = comClient.detectDominantLanguage(request)
            response.languages?.forEach { lang ->
                println("Language is ${lang.languageCode}")
            }
    }
}
//snippet-end:[comprehend.kotlin.detect_language.main]
