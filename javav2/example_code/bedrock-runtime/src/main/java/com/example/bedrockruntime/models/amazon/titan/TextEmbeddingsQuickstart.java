// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.titan;

import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

// snippet-start:[bedrock-runtime.java2.InvokeModel_TitanTextEmbeddingsV2_Quickstart]
// Generate and print an embedding with Amazon Titan Text Embeddings.
public class TextEmbeddingsQuickstart {

    public static void main(String[] args) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Set the model ID, e.g., Titan Text Embeddings V2.
        var modelId = "amazon.titan-embed-text-v2:0";

        // The text to convert into an embedding.
        var inputText = "Please recommend books with a theme similar to the movie 'Inception'.";

        // Create a JSON payload using the model's native structure.
        var request = new JSONObject().put("inputText", inputText);

        // Encode and send the request.
        var response = client.invokeModel(req -> req
                .body(SdkBytes.fromUtf8String(request.toString()))
                .modelId(modelId));

        // Decode the model's native response body.
        var nativeResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the generated embedding.
        var embedding = nativeResponse.getJSONArray("embedding");
        System.out.println(embedding);
    }
}
// snippet-end:[bedrock-runtime.java2.InvokeModel_TitanTextEmbeddingsV2_Quickstart]
