// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime

// snippet-start:[bedrock-runtime.kotlin.InvokeModel_AmazonTitanText]
import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Before running this Kotlin code example, set up your development environment, including your credentials.
 *
 * This example demonstrates how to invoke the Titan Text model (amazon.titan-text-lite-v1).
 * Remember that you must enable the model before you can use it. See notes in the README.md file.
 *
 * For more information, see the following documentation topic:
 * https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/setup.html
 */
suspend fun main() {
    val prompt = """
        Write a short, funny story about a time-traveling cat who
        ends up in ancient Egypt at the time of the pyramids.
    """.trimIndent()

    val response = invokeModel(prompt, "amazon.titan-text-lite-v1")
    println("Generated story:\n$response")
}

suspend fun invokeModel(prompt: String, modelId: String): String {
    BedrockRuntimeClient { region = "eu-central-1" }.use { client ->
        val request = InvokeModelRequest {
            this.modelId = modelId
            contentType = "application/json"
            accept = "application/json"
            body = """
                {
                    "inputText": "${prompt.replace(Regex("\\s+"), " ").trim()}",
                    "textGenerationConfig": {
                        "maxTokenCount": 1000,
                        "stopSequences": [],
                        "temperature": 1,
                        "topP": 0.7
                    }
                }
            """.trimIndent().toByteArray()
        }

        val response = client.invokeModel(request)
        val responseBody = response.body.toString(Charsets.UTF_8)

        val jsonParser = Json { ignoreUnknownKeys = true }
        return jsonParser
            .decodeFromString<BedrockResponse>(responseBody)
            .results
            .first()
            .outputText
    }
}

@Serializable
private data class BedrockResponse(val results: List<Result>)

@Serializable
private data class Result(val outputText: String)
// snippet-end:[bedrock-runtime.kotlin.InvokeModel_AmazonTitanText]
