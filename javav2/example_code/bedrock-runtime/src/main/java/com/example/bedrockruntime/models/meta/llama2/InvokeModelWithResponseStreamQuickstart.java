// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.meta.llama2;

import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler;

// snippet-start:[bedrock-runtime.java2.InvokeModelWithResponseStream_Llama2_Quickstart]
// Send a prompt to Meta Llama 2 and print the response stream in real-time.
public class InvokeModelWithResponseStreamQuickstart {

    public static void main(String[] args) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeAsyncClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Set the model ID, e.g., Llama 2 Chat 13B.
        var modelId = "meta.llama2-13b-chat-v1";

        // Define the user message to send.
        var userMessage = "Describe the purpose of a 'hello world' program in one line.";

        // Embed the message in Llama 2's prompt format.
        var prompt = "<s>[INST] " + userMessage + " [/INST]";

        // Create a JSON payload using the model's native structure.
        var request = new JSONObject()
                .put("prompt", prompt)
                // Optional inference parameters:
                .put("max_gen_len", 512)
                .put("temperature", 0.5F)
                .put("top_p", 0.9F);

        // Create a handler to extract and print the response text in real-time.
        var streamHandler = InvokeModelWithResponseStreamResponseHandler.builder()
                .subscriber(event -> event.accept(
                        InvokeModelWithResponseStreamResponseHandler.Visitor.builder()
                                .onChunk(c -> {
                                    var chunk = new JSONObject(c.bytes().asUtf8String());
                                    if (chunk.has("generation")) {
                                        System.out.print(chunk.getString("generation"));
                                    }
                                }).build())
                ).build();

        // Encode and send the request. Let the stream handler process the response.
        client.invokeModelWithResponseStream(req -> req
                .body(SdkBytes.fromUtf8String(request.toString()))
                .modelId(modelId), streamHandler
        ).join();
    }
}
// Learn more about the Llama 2 prompt format at:
// https://llama.meta.com/docs/model-cards-and-prompt-formats/meta-llama-2

// snippet-end:[bedrock-runtime.java2.InvokeModelWithResponseStream_Llama2_Quickstart]
