// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.nova.canvas

// snippet-start:[bedrock-runtime.kotlin.InvokeModel_AmazonNovaImageGeneration]

import aws.sdk.kotlin.services.bedrockruntime.BedrockRuntimeClient
import aws.sdk.kotlin.services.bedrockruntime.model.InvokeModelRequest
import com.example.bedrockruntime.libs.ImageTools.displayImage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

/**
 * This example demonstrates how to use Amazon Nova Canvas to generate images.
 * It shows how to:
 * - Set up the Amazon Bedrock runtime client
 * - Configure the image generation parameters
 * - Send a request to generate an image
 * - Process the response and display the generated image
 */
suspend fun main() {
    println("Generating image. This may take a few seconds...")
    val imageData = invokeModel()
    displayImage(imageData)
}

// Data class for parsing the model's response
@Serializable
private data class Response(val images: List<String>)

// Configure JSON parser to ignore unknown fields in the response
private val json = Json { ignoreUnknownKeys = true }

suspend fun invokeModel(): ByteArray {
    // Create and configure the Bedrock runtime client
    BedrockRuntimeClient { region = "us-east-1" }.use { client ->

        // Specify the model ID. For the latest available models, see:
        // https://docs.aws.amazon.com/bedrock/latest/userguide/models-supported.html
        val modelId = "amazon.nova-canvas-v1:0"

        // Configure the generation parameters and create the request
        // First, set the main parameters:
        // - prompt: Text description of the image to generate
        // - seed: Random number for reproducible generation (0 to 858,993,459)
        val prompt = "A stylized picture of a cute old steampunk robot"
        val seed = (0..858_993_459).random()

        // Then, create the request using a template with the following structure:
        // - taskType: TEXT_IMAGE (specifies text-to-image generation)
        // - textToImageParams: Contains the text prompt
        // - imageGenerationConfig: Contains optional generation settings (seed, quality, etc.)
        // For a list of available request parameters, see:
        // https://docs.aws.amazon.com/nova/latest/userguide/image-gen-req-resp-structure.html
        val request = """
            {
                "taskType": "TEXT_IMAGE",
                "textToImageParams": {
                    "text": "$prompt"
                },
                "imageGenerationConfig": {
                    "seed": $seed,
                    "quality": "standard"
                }
            }
        """.trimIndent()

        // Send the request and process the model's response
        runCatching {
            // Send the request to the model
            val response = client.invokeModel(
                InvokeModelRequest {
                    this.modelId = modelId
                    body = request.toByteArray()
                },
            )

            // Parse the response and extract the generated image
            val jsonResponse = response.body.toString(Charsets.UTF_8)
            val parsedResponse = json.decodeFromString<Response>(jsonResponse)

            // Extract the generated image and return it as a byte array for better handling
            val base64Image = parsedResponse.images.first()
            return Base64.getDecoder().decode(base64Image)
        }.getOrElse { error ->
            System.err.println("ERROR: Can't invoke '$modelId'. Reason: ${error.message}")
            throw RuntimeException("Failed to generate image with model $modelId", error)
        }
    }
}

// snippet-end:[bedrock-runtime.kotlin.InvokeModel_AmazonNovaImageGeneration]
