// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.titan;

import com.example.bedrockruntime.libs.demo.DemoRunner;
import com.example.bedrockruntime.libs.demo.scenarios.SystemPromptScenario;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.io.IOException;
import java.util.List;

/**
 * This program demonstrates how to use InvokeModel with Amazon Titan Text Embeddings G1 on Amazon Bedrock.
 * <p>
 * For more examples in different programming languages check out the Amazon Bedrock User Guide at:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
 */

public class TextEmbeddingsG1Scenarios {
    // snippet-start:[bedrock-runtime.java2.InvokeModel_TitanTextEmbeddingsG1_AdditionalFields]

    /**
     * Invoke Amazon Titan Text Embeddings G1 and print the response.
     *
     * @param inputText - The text to convert to an embedding.
     * @return The {@link JSONObject} representing the model's response.
     */
    public static JSONObject invokeModel(String inputText) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Set the model ID, e.g., Titan Text Embeddings G1.
        var modelId = "amazon.titan-embed-text-v1";

        // Format the request payload using Anthropic's native structure.
        var nativeRequest = "{\"inputText\": \"" + inputText + "\"}";

        // Encode and send the request.
        var response = client.invokeModel(request -> {
            request.body(SdkBytes.fromUtf8String(nativeRequest));
            request.modelId(modelId);
        });

        // Decode the model's response.
        var modelResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the generated embedding and the input text token count.
        var embedding = modelResponse.getJSONArray("embedding");
        var inputTokenCount = modelResponse.getBigInteger("inputTextTokenCount");
        System.out.println("Embedding: " + embedding);
        System.out.println("\nInput token count: " + inputTokenCount);

        // Return the model's native response.
        return modelResponse;
    }
    // snippet-end:[bedrock-runtime.java2.InvokeModel_TitanTextEmbeddingsG1_AdditionalFields]

    public static void main(String[] args) throws IOException {
        new DemoRunner(List.of(
                new SystemPromptScenario(TextG1Scenarios::invokeWithSystemPrompt)
        )).run();
    }
}
