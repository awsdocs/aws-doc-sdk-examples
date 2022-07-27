// snippet-sourcedescription:[DetectSentiment.kt demonstrates how to detect sentiments in the text.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Comprehend]

package com.kotlin.comprehend

// snippet-start:[comprehend.kotlin.detect_language.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectDominantLanguageRequest
// snippet-end:[comprehend.kotlin.detect_language.import]

suspend fun main() {

    val text = "Il pleut aujourd'hui à Seattle"
    detectTheDominantLanguage(text)
}

// snippet-start:[comprehend.kotlin.detect_language.main]
suspend fun detectTheDominantLanguage(textVal: String) {

    val request = DetectDominantLanguageRequest {
        text = textVal
    }

    ComprehendClient { region = "us-east-1" }.use { comClient ->
        val response = comClient.detectDominantLanguage(request)
        response.languages?.forEach { lang ->
            println("Language is ${lang.languageCode}")
        }
    }
}
// snippet-end:[comprehend.kotlin.detect_language.main]
