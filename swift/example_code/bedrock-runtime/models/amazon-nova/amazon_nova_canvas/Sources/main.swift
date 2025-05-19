// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
// snippet-start:[swift.example_code.bedrock-runtime.InvokeModel_AmazonNovaImageGeneration]
// Use the native inference API to create an image with Amazon Nova Canvas

import AWSBedrockRuntime
import Foundation

struct NovaImageOutput: Decodable {
    let images: [Data]
}

func generateImage(_ textPrompt: String, to path: String) async throws {
    // Create a Bedrock Runtime client in the AWS Region you want to use.
    let config =
        try await BedrockRuntimeClient.BedrockRuntimeClientConfiguration(
            region: "us-east-1"
        )

    let client = BedrockRuntimeClient(config: config)

    // Set the model ID.
    let modelId = "amazon.nova-canvas-v1:0"

    //  Format the request payload using the model's native structure.
    let input = InvokeModelInput(
        accept: "application/json",
        body: """
            {
                "textToImageParams": {
                    "text": "\(textPrompt)"
                },
                "taskType": "TEXT_IMAGE",
                "imageGenerationConfig": {
                    "cfgScale": 8,
                    "seed": 42,
                    "quality": "standard",
                    "width": 1024,
                    "height": 1024,
                    "numberOfImages": 1
                }
            }
            """.data(using: .utf8),
        modelId: modelId
    )

    // Invoke the model with the request.
    let response = try await client.invokeModel(input: input)

    // Decode the response body.
    let titanImage = try JSONDecoder().decode(NovaImageOutput.self, from: response.body!)

    // Extract the image data.
    let data = titanImage.images.first
    guard let data = data else {
        print("No image data found")
        return
    }

    // Save the generated image to a local folder.
    let fileURL = URL(fileURLWithPath: path)
    try data.write(to: fileURL)
    print("Image is saved at \(path)")
}

// snippet-end:[swift.example_code.bedrock-runtime.InvokeModel_AmazonNovaImageGeneration]

do {
    try await generateImage(
        "A tabby cat in a teacup", to: "/Users/monadierickx/Desktop/img/nova_canvas.png"
    )
} catch {
    print("An error occurred: \(error)")
}
