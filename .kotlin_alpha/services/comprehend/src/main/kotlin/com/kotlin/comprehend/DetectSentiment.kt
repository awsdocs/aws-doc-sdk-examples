// snippet-sourcedescription:[DetectSentiment.kt demonstrates how to detect sentiments in the text.]
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

//snippet-start:[comprehend.kotlin.detect_sentiment.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectSentimentRequest
import aws.sdk.kotlin.services.comprehend.model.LanguageCode
import aws.sdk.kotlin.services.comprehend.model.ComprehendException
import kotlin.system.exitProcess
//snippet-end:[comprehend.kotlin.detect_sentiment.import]


suspend fun main() {

    val comprehendClient = ComprehendClient({region="us-east-1"})
    val text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing."
    detectSentiments(comprehendClient,text)
    comprehendClient.close()
}

//snippet-start:[comprehend.kotlin.detect_sentiment.main]
suspend fun detectSentiments(comClient: ComprehendClient, textVal: String) {
        try {

            val detectSentimentRequest = DetectSentimentRequest{
                text = textVal
                languageCode = LanguageCode.fromValue("en")
            }

            val resp = comClient.detectSentiment(detectSentimentRequest)
            println("The Neutral value is ${resp.sentimentScore?.neutral}")

        } catch (ex: ComprehendException) {
            println(ex.message)
            exitProcess(0)
        }
}
//snippet-end:[comprehend.kotlin.detect_sentiment.main]