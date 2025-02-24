// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.titan.text

// snippet-start:[bedrock-runtime.kotlin.InvokeModel_AmazonTitanText]

import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelRequest
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * This example demonstrates how to use the Amazon Titan foundation models to generate text.
 * It shows how to:
 * - Set up the Amazon Bedrock runtime client
 * - Create a request payload
 * - Configure and send a request
 * - Process the response
 */
suspend fun main() {
    invokeModel().also { println(it) }
}

// Data class for parsing the model's response
@Serializable
private data class BedrockResponse(val results: List<Result>) {
    @Serializable
    data class Result(
        val outputText: String
    )
}


// Initialize JSON parser with relaxed configuration
private val json = Json { ignoreUnknownKeys = true }

suspend fun invokeModel(): String {
    // Create and configure the Bedrock runtime client
    BedrockRuntimeClient { region = "us-east-1" }.use { client ->

        // Specify the model ID. For the latest available models, see:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html
        val modelId = "amazon.titan-text-lite-v1"

        // Create the request payload with optional configuration parameters
        // For detailed parameter descriptions, see:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-text.html
        val prompt = "Describe the purpose of a 'hello world' program in one line."
        val request = """
            {
                "inputText": "$prompt",
                "textGenerationConfig": {
                    "maxTokenCount": 500,
                    "temperature": 0.5
                }
            }
            """.trimIndent()

        // Send the request and process the model's response
        runCatching {
            // Send the request to the model
            val response = client.invokeModel(InvokeModelRequest {
                this.modelId = modelId
                body = request.toByteArray()
            })

            // Convert the response bytes to a JSON string
            val jsonResponse = response.body.toString(Charsets.UTF_8)

            // Parse the JSON into a Kotlin object
            val parsedResponse = json.decodeFromString<BedrockResponse>(jsonResponse)

            // Extract and return the generated text
            return parsedResponse.results.firstOrNull()!!.outputText

        }.getOrElse { error ->
            error.message?.let { msg ->
                System.err.println("ERROR: Can't invoke '$modelId'. Reason: $msg")
            }
            throw RuntimeException("Failed to generate text with model $modelId", error)
        }
    }
}

// snippet-end:[bedrock-runtime.kotlin.InvokeModel_AmazonTitanText]
