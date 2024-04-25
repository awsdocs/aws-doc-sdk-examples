// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.meta.llama3;

import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.text.MessageFormat;

// snippet-start:[bedrock-runtime.java2.InvokeModel_Llama3_Quickstart]
// Send a prompt to Meta Llama 3 and print the response.
public class InvokeModelQuickstart {

    public static void main(String[] args) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Set the model ID, e.g., Llama 3 8B Instruct.
        var modelId = "meta.llama3-8b-instruct-v1:0";

        // Define the user message to send.
        var userMessage = "Describe the purpose of a 'hello world' program in one line.";

        // Embed the message in Llama 3's prompt format.
        var prompt = MessageFormat.format("""
                <|begin_of_text|>
                <|start_header_id|>user<|end_header_id|>
                {0}
                <|eot_id|>
                <|start_header_id|>assistant<|end_header_id|>
                """, userMessage);

        // Create a JSON payload using the model's native structure.
        var request = new JSONObject()
                .put("prompt", prompt)
                // Optional inference parameters:
                .put("max_gen_len", 512)
                .put("temperature", 0.5F)
                .put("top_p", 0.9F);

        // Encode and send the request.
        var response = client.invokeModel(req -> req
                .body(SdkBytes.fromUtf8String(request.toString()))
                .modelId(modelId));

        // Decode the native response body.
        var nativeResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the response text.
        var responseText = nativeResponse.getString("generation");
        System.out.println(responseText);
    }
}
// Learn more about the Llama 3 prompt format at:
// https://llama.meta.com/docs/model-cards-and-prompt-formats/meta-llama-3/#special-tokens-used-with-meta-llama-3

// snippet-end:[bedrock-runtime.java2.InvokeModel_Llama3_Quickstart]
