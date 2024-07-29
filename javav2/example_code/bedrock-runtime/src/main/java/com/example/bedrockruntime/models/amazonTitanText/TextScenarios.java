// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazonTitanText;

import com.example.bedrockruntime.libs.demo.DemoRunner;
import com.example.bedrockruntime.libs.demo.scenarios.SystemPromptScenario;
import com.example.bedrockruntime.libs.demo.scenarios.TitanConversationScenario;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.io.IOException;
import java.util.List;

/**
 * This program demonstrates how to use InvokeModel with Amazon Titan Text models on Amazon Bedrock,
 * using Titan's native request/response structure.
 * <p>
 * For more examples in different programming languages check out the Amazon Bedrock User Guide at:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
 */

public class TextScenarios {
    // snippet-start:[bedrock-runtime.java2.InvokeModel_TitanText_SingleMessage]

    /**
     * Invoke Titan Text with a system prompt and additional inference parameters,
     * using Titan's native request/response structure.
     *
     * @param userPrompt   - The text prompt to send to the model.
     * @param systemPrompt - A system prompt to provide additional context and instructions.
     * @return The {@link JSONObject} representing the model's response.
     */
    public static JSONObject invokeWithSystemPrompt(String userPrompt, String systemPrompt) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Set the model ID, e.g., Titan Text Premier.
        var modelId = "amazon.titan-text-premier-v1:0";

        /* Assemble the input text.
         * For best results, use the following input text format:
         *     {{ system instruction }}
         *     User: {{ user input }}
         *     Bot:
         */
        var inputText = """
                %s
                User: %s
                Bot:
                """.formatted(systemPrompt, userPrompt);

        // Format the request payload using the model's native structure.
        var nativeRequest = new JSONObject()
                .put("inputText", inputText)
                .put("textGenerationConfig", new JSONObject()
                        .put("maxTokenCount", 512)
                        .put("temperature", 0.7F)
                        .put("topP", 0.9F)
                )
                .toString();

        // Encode and send the request.
        var response = client.invokeModel(request -> {
            request.body(SdkBytes.fromUtf8String(nativeRequest));
            request.modelId(modelId);
        });

        // Decode the native response body.
        var nativeResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the response text.
        var responseText = nativeResponse.getJSONArray("results").getJSONObject(0).getString("outputText");
        System.out.println(responseText);

        // Return the model's native response.
        return nativeResponse;
    }
    // snippet-end:[bedrock-runtime.java2.InvokeModel_TitanText_SingleMessage]

    // snippet-start:[bedrock-runtime.java2.InvokeModel_TitanText_Conversation]

    /**
     * Create a chat-like experience with a conversation history, using Titan's native
     * request/response structure.
     *
     * @param prompt       - The text prompt to send to the model.
     * @param conversation - A String representing previous conversational turns in the format
     *                     User: {{ previous user prompt}}
     *                     Bot: {{ previous model response }}
     *                     ...
     * @return The {@link JSONObject} representing the model's response.
     */
    public static JSONObject invokeWithConversation(String prompt, String conversation) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Set the model ID, e.g., Titan Text Premier.
        var modelId = "amazon.titan-text-premier-v1:0";

        /* Append the new prompt to the conversation.
         * For best results, use the following text format:
         *     User: {{ previous user prompt}}
         *     Bot: {{ previous model response }}
         *     User: {{ new user prompt }}
         *     Bot: """
         */
        conversation = conversation + """
                %nUser: %s
                Bot:
                """.formatted(prompt);

        // Format the request payload using the model's native structure.
        var nativeRequest = new JSONObject().put("inputText", conversation);

        // Encode and send the request.
        var response = client.invokeModel(request -> {
            request.body(SdkBytes.fromUtf8String(nativeRequest.toString()));
            request.modelId(modelId);
        });

        // Decode the native response body.
        var nativeResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the response text.
        var responseText = nativeResponse.getJSONArray("results").getJSONObject(0).getString("outputText");
        System.out.println(responseText);

        // Return the model's native response.
        return nativeResponse;
    }
    // snippet-end:[bedrock-runtime.java2.InvokeModel_TitanText_Conversation]


    public static void main(String[] args) throws IOException {
        new DemoRunner(List.of(
                new SystemPromptScenario(TextScenarios::invokeWithSystemPrompt),
                new TitanConversationScenario(TextScenarios::invokeWithConversation)
        )).run();
    }
}
