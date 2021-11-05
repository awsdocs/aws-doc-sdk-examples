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
import aws.sdk.kotlin.services.translate.model.TranslateException
import kotlin.system.exitProcess
// snippet-end:[translate.kotlin._text.import]

suspend fun main(){

    val translateClient = TranslateClient { region = "us-east-1" }
    textTranslate(translateClient)
    translateClient.close()
}

// snippet-start:[translate.kotlin._text.main]
suspend fun textTranslate(translateClient: TranslateClient) {
        try {
            val textRequest = TranslateTextRequest {
                 sourceLanguageCode = "en"
                 targetLanguageCode = "fr"
                 text = "Its a sunny day today"
            }

            val textResponse = translateClient.translateText(textRequest)
            println(textResponse.translatedText)

        } catch (ex: TranslateException) {
            println(ex.message)
            exitProcess(0)
        }
 }
// snippet-end:[translate.kotlin._text.main]