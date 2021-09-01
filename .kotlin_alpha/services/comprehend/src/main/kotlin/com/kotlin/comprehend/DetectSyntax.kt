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

//snippet-start:[comprehend.kotlin.detect_syntax.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectSyntaxRequest
import aws.sdk.kotlin.services.comprehend.model.SyntaxLanguageCode
import aws.sdk.kotlin.services.comprehend.model.ComprehendException
import kotlin.system.exitProcess
//snippet-end:[comprehend.kotlin.detect_syntax.import]

/**
To run this Kotlin code example, ensure that you have setup your development environment,
including your credentials.

For information, see this documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {

    val comprehendClient = ComprehendClient({region="us-east-1"})
    val text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing."
    detectAllSyntax(comprehendClient,text)
    comprehendClient.close()
}

//snippet-start:[comprehend.kotlin.detect_syntax.main]
suspend fun detectAllSyntax(comClient: ComprehendClient, textVal: String?) {
        try {

             val detectSyntaxRequest = DetectSyntaxRequest {
                 text = textVal
                 languageCode = SyntaxLanguageCode.fromValue("en")
             }

            val resp = comClient.detectSyntax(detectSyntaxRequest)
            val syntaxTokens = resp.syntaxTokens

            if (syntaxTokens != null) {

                for (token in syntaxTokens) {
                    println("Language is ${token.text}")
                    println("Part of speech is ${token.partOfSpeech.toString()}")
                }
            }

        } catch (ex: ComprehendException) {
            println(ex.message)
            comClient.close()
            exitProcess(0)
        }
}
//snippet-end:[comprehend.kotlin.detect_syntax.main]
