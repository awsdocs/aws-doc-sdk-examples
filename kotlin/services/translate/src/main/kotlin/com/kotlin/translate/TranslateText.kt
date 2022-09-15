// snippet-sourcedescription:[TranslateText.kt demonstrates how to translate text from one language to another.]
// snippet-keyword:[SDK for Kotlin]
// snippet-service:[Amazon Translate]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.translate

// snippet-start:[translate.kotlin._text.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.TranslateTextRequest
// snippet-end:[translate.kotlin._text.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    textTranslate()
}

// snippet-start:[translate.kotlin._text.main]
suspend fun textTranslate() {

    val textRequest = TranslateTextRequest {
        sourceLanguageCode = "en"
        targetLanguageCode = "fr"
        text = "Its a sunny day today"
    }

    TranslateClient { region = "us-west-2" }.use { translateClient ->
        val textResponse = translateClient.translateText(textRequest)
        println(textResponse.translatedText)
    }
}
// snippet-end:[translate.kotlin._text.main]
