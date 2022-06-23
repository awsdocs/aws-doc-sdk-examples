// snippet-sourcedescription:[DetectSentiment.kt demonstrates how to detect sentiments in the text.]
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

//snippet-start:[comprehend.kotlin.detect_sentiment.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectSentimentRequest
import aws.sdk.kotlin.services.comprehend.model.LanguageCode
//snippet-end:[comprehend.kotlin.detect_sentiment.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing."
    detectSentiments(text)

}

//snippet-start:[comprehend.kotlin.detect_sentiment.main]
suspend fun detectSentiments( textVal: String) {

    val request =  DetectSentimentRequest{
        text = textVal
        languageCode = LanguageCode.fromValue("en")
    }

    ComprehendClient { region = "us-east-1" }.use { comClient ->
        val resp = comClient.detectSentiment(request)
        println("The Neutral value is ${resp.sentimentScore?.neutral}")
    }
}
//snippet-end:[comprehend.kotlin.detect_sentiment.main]