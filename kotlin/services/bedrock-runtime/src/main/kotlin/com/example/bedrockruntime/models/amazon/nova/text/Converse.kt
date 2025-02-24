// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.nova.text

// snippet-start:[bedrock-runtime.kotlin.Converse_AmazonNovaText]

import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.ContentBlock
import aws.sdk.kotlin.services.bedrockruntime.model.ConversationRole
import aws.sdk.kotlin.services.bedrockruntime.model.ConverseRequest
import aws.sdk.kotlin.services.bedrockruntime.model.Message

/**
 * This example demonstrates how to use the Amazon Nova foundation models to generate text.
 * It shows how to:
 * - Set up the Amazon Bedrock runtime client
 * - Create a message
 * - Configure and send a request
 * - Process the response
 *
 * @throws RuntimeException if the model invocation fails
 */
suspend fun main() {
    converse().also { println(it) }
}

suspend fun converse(): String {
    // Step 1: Create the Amazon Bedrock runtime client
    // The runtime client handles the communication with AI models on Amazon Bedrock
    BedrockRuntimeClient { region = "us-east-1" }.use { client ->

        // Step 2: Specify which model to use
        // Available Amazon Nova models and their characteristics:
        // - Amazon Nova Micro: Text-only model optimized for lowest latency and cost
        // - Amazon Nova Lite:  Fast, low-cost multimodal model for image, video, and text
        // - Amazon Nova Pro:   Advanced multimodal model balancing accuracy, speed, and cost
        //
        // For the latest available models, see:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html
        val modelId = "amazon.nova-lite-v1:0"

        // Step 3: Create the message
        // The message includes the text prompt and specifies that it comes from the user
        val inputText = "Describe the purpose of a 'hello world' program in one line."
        val message = Message {
            role = ConversationRole.User
            content = listOf(ContentBlock.Text(inputText))
        }

        // Step 4: Configure the request
        // Optional parameters to control the model's response:
        // - maxTokens: maximum number of tokens to generate
        // - temperature: randomness (max: 1.0, default: 0.7)
        //   OR
        // - topP: diversity of word choice (max: 1.0, default: 0.9)
        // Note: Use either temperature OR topP, but not both
        val request = ConverseRequest {
            this.modelId = modelId
            messages = listOf(message)
            inferenceConfig {
                maxTokens = 500     // The maximum response length
                temperature = 0.5F  // Using temperature for randomness control
                // topP = 0.8F      // Alternative: use topP instead of temperature
            }
        }

        // Step 5: Send and process the request
        // - Send the request to the model
        // - Extract and return the generated text
        runCatching {
            val response = client.converse(request)
            return response.output!!.asMessage().content.first().asText()

        }.getOrElse { error ->
            error.message?.let { e -> System.err.println("ERROR: Can't invoke '$modelId'. Reason: $e") }
            throw RuntimeException("Failed to generate text with model $modelId", error)
        }
    }
}
// snippet-end:[bedrock-runtime.kotlin.Converse_AmazonNovaText]
