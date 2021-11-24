//snippet-sourcedescription:[TranslateText.kt demonstrates how to translate text from one language to another.]
//snippet-keyword:[SDK for Kotlin]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Translate]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2021]
//snippet-sourceauthor:[scmacdon-aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.translate

// snippet-start:[translate.kotlin._text.import]
import aws.sdk.kotlin.services.translate.TranslateClient
import aws.sdk.kotlin.services.translate.model.TranslateTextRequest
// snippet-end:[translate.kotlin._text.import]

suspend fun main(){
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