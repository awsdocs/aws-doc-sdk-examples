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
 *
 * @throws RuntimeException if the model invocation fails
 */
suspend fun main() {
    invokeModel().also { println(it) }
}

// Initialize JSON parser with relaxed configuration
private val json = Json { ignoreUnknownKeys = true }

suspend fun invokeModel(): String {
    // Step 1: Create the Amazon Bedrock runtime client
    // The runtime client handles the communication with AI models on Amazon Bedrock
    BedrockRuntimeClient { region = "us-east-1" }.use { client ->

        // Step 2: Specify which model to use
        // Available Amazon Titan models and their characteristics:
        // - Titan Text Lite: Fast, cost-effective text generation
        // - Titan Text Express: Balanced performance and cost
        // - Titan Text Large: Advanced capabilities for complex tasks
        //
        // For the latest available models, see:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html
        val modelId = "amazon.titan-text-lite-v1"

        // Step 3: Create the request payload
        // Optional parameters to control the model's response:
        // - maxTokenCount: maximum number of tokens to generate
        // - temperature: randomness (max: 1.0, default: 0.7)
        //   OR
        // - topP: diversity of word choice (max: 1.0, default: 0.9)
        // Note: Use either temperature OR topP, but not both
        //
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

        // Step 4: Send and process the request
        // - Send the request to the model
        // - Parse the JSON response
        // - Extract and return the generated text
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

@Serializable
private data class BedrockResponse(val results: List<Result>)

@Serializable
private data class Result(val outputText: String)

// snippet-end:[bedrock-runtime.kotlin.InvokeModel_AmazonTitanText]
