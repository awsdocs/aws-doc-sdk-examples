// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime;

import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler;
import software.amazon.awssdk.services.bedrockruntime.model.ResponseStream;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 * <p>
 * For more information, see the following documentation topic:
 * <p>
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class Claude3 {
    public static void main(String[] args) {
        System.out.println("=".repeat(67));
        System.out.println("Welcome to the Amazon Bedrock Runtime Demo with Anthropic Claude 3.");
        System.out.println("=".repeat(67));

        var prompt = "Hi, how are you?.";
        System.out.println("Prompt: " + prompt);

        System.out.println("-".repeat(67));
        System.out.println("Streaming response:");
        try {
            JSONObject messagesApiResponse = invokeModelWithResponseStream(prompt);

            System.out.println("\n" + "-".repeat(67));
            System.out.println("Structured response:");
            System.out.println(messagesApiResponse.toString(2));

        } catch (Exception e) {
            System.out.println("Couldn't invoke model using the Messages API, here's why: " + e.getMessage());
        }
    }

    // snippet-start:[bedrock-runtime.java2.invoke_claude3_with_response_stream.main]

    /**
     * Invokes Anthropic Claude 3 Haiku and processes the response stream.
     *
     * @param prompt The prompt for the model to complete.
     * @return A JSON object containing the complete response along with some metadata.
     */
    public static JSONObject invokeModelWithResponseStream(String prompt) {
        BedrockRuntimeAsyncClient client = BedrockRuntimeAsyncClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .build();

        String modelId = "anthropic.claude-3-haiku-20240307-v1:0";

        // Prepare the JSON payload for the Messages API request
        var payload = new JSONObject()
                .put("anthropic_version", "bedrock-2023-05-31")
                .put("max_tokens", 1000)
                .append("messages", new JSONObject()
                        .put("role", "user")
                        .append("content", new JSONObject()
                                .put("type", "text")
                                .put("text", prompt)
                        ));

        // Create the request object using the payload and the model ID
        var request = InvokeModelWithResponseStreamRequest.builder()
                .contentType("application/json")
                .body(SdkBytes.fromUtf8String(payload.toString()))
                .modelId(modelId)
                .build();

        // Create a handler to print the stream in real-time and add metadata to a response object
        JSONObject structuredResponse = new JSONObject();
        var handler = createMessagesApiResponseStreamHandler(structuredResponse);

        // Invoke the model with the request payload and the response stream handler
        client.invokeModelWithResponseStream(request, handler).join();

        return structuredResponse;
    }

    private static InvokeModelWithResponseStreamResponseHandler createMessagesApiResponseStreamHandler(JSONObject structuredResponse) {
        AtomicReference<String> completeMessage = new AtomicReference<>("");

        Consumer<ResponseStream> responseStreamHandler = event -> event.accept(InvokeModelWithResponseStreamResponseHandler.Visitor.builder()
                .onChunk(c -> {
                    // Decode the chunk
                    var chunk = new JSONObject(c.bytes().asUtf8String());

                    // The Messages API returns different types:
                    var chunkType = chunk.getString("type");
                    if ("message_start".equals(chunkType)) {
                        // The first chunk contains information about the message role
                        String role = chunk.optJSONObject("message").optString("role");
                        structuredResponse.put("role", role);

                    } else if ("content_block_delta".equals(chunkType)) {
                        // These chunks contain the text fragments
                        var text = chunk.optJSONObject("delta").optString("text");
                        // Print the text fragment to the console ...
                        System.out.print(text);
                        // ... and append it to the complete message
                        completeMessage.getAndUpdate(current -> current + text);

                    } else if ("message_delta".equals(chunkType)) {
                        // This chunk contains the stop reason
                        var stopReason = chunk.optJSONObject("delta").optString("stop_reason");
                        structuredResponse.put("stop_reason", stopReason);

                    } else if ("message_stop".equals(chunkType)) {
                        // The last chunk contains the metrics
                        JSONObject metrics = chunk.optJSONObject("amazon-bedrock-invocationMetrics");
                        structuredResponse.put("metrics", new JSONObject()
                                .put("inputTokenCount", metrics.optString("inputTokenCount"))
                                .put("outputTokenCount", metrics.optString("outputTokenCount"))
                                .put("firstByteLatency", metrics.optString("firstByteLatency"))
                                .put("invocationLatency", metrics.optString("invocationLatency")));
                    }
                })
                .build());

        return InvokeModelWithResponseStreamResponseHandler.builder()
                .onEventStream(stream -> stream.subscribe(responseStreamHandler))
                .onComplete(() ->
                        // Add the complete message to the response object
                        structuredResponse.append("content", new JSONObject()
                                .put("type", "text")
                                .put("text", completeMessage.get())))
                .build();
    }
    // snippet-end:[bedrock-runtime.java2.invoke_claude3_with_response_stream.main]
}
