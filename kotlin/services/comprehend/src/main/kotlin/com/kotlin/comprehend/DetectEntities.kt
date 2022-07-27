// snippet-sourcedescription:[DetectEntities.kt demonstrates how to retrieve named entities from within specified text.]
// snippet-keyword:[AWS SDK for Kotlin]
// snippet-service:[Amazon Comprehend]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.kotlin.comprehend

// snippet-start:[comprehend.kotlin.detect_entities.import]
import aws.sdk.kotlin.services.comprehend.ComprehendClient
import aws.sdk.kotlin.services.comprehend.model.DetectEntitiesRequest
import aws.sdk.kotlin.services.comprehend.model.LanguageCode
// snippet-end:[comprehend.kotlin.detect_entities.import]

/**
Before running this Kotlin code example, set up your development environment,
including your credentials.

For more information, see the following documentation topic:
https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
*/

suspend fun main() {

    val text = "Amazon.com, Inc. is located in Seattle, WA and was founded July 5th, 1994 by Jeff Bezos, allowing customers to buy everything from books to blenders. Seattle is north of Portland and south of Vancouver, BC. Other notable Seattle - based companies are Starbucks and Boeing."
    detectAllEntities(text)
}

// snippet-start:[comprehend.kotlin.detect_entities.main]
suspend fun detectAllEntities(textVal: String) {

    val request = DetectEntitiesRequest {
        text = textVal
        languageCode = LanguageCode.fromValue("en")
    }
    ComprehendClient { region = "us-east-1" }.use { comClient ->
        val response = comClient.detectEntities(request)
        response.entities?.forEach { entity ->
            println("Entity text is ${entity.text}")
        }
    }
}
// snippet-end:[comprehend.kotlin.detect_entities.main]
