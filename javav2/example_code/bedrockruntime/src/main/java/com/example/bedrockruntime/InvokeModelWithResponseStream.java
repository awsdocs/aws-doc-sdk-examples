// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[InvokeModel.java demonstrates how to invoke a model with Amazon Bedrock and process the response stream.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Bedrock]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.bedrockruntime;

// snippet-start:[bedrockruntime.java2.invoke_model_with_response_stream.import]
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelWithResponseStreamResponseHandler;
import software.amazon.awssdk.services.bedrockruntime.model.ResponseStream;

import java.util.concurrent.CompletableFuture;
// snippet-end:[bedrockruntime.java2.invoke_model_with_response_stream.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InvokeModelWithResponseStream {

    public static void main(String[] args) {

        Region region = Region.US_EAST_1;
        BedrockRuntimeAsyncClient bedrockRuntimeAsyncClient = BedrockRuntimeAsyncClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String prompt = "What is a large-language model?";

        invokeModel(bedrockRuntimeAsyncClient, prompt);
    }

    // snippet-start:[bedrockruntime.java2.invoke_model_with_response_stream.main]
    public static String invokeModel(BedrockRuntimeAsyncClient bedrockRuntimeAsyncClient, String prompt) {

        try {

            double temperature = 0.8;
            int maxTokensToSample = 300;

            JSONObject payload = new JSONObject()
                    .put("prompt", "Human: " + prompt + " Assistant:")
                    .put("temperature", temperature)
                    .put("max_tokens_to_sample", maxTokensToSample);

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelWithResponseStreamRequest request = InvokeModelWithResponseStreamRequest.builder()
                    .modelId("anthropic.claude-v2")
                    .contentType("application/json")
                    .accept("application/json")
                    .body(body)
                    .build();

            InvokeModelWithResponseStreamResponseHandler.Visitor visitor =
                    InvokeModelWithResponseStreamResponseHandler.Visitor.builder()
                            .onDefault((event) -> System.out.println("\n\nDefault: " + event.toString()))
                            .onChunk((chunk) -> {
                                JSONObject json = new JSONObject(chunk.bytes().asUtf8String());
                                System.out.print(json.getString("completion"));
                            })
                            .build();

            InvokeModelWithResponseStreamResponseHandler responseHandler =
                    InvokeModelWithResponseStreamResponseHandler.builder()
                            .onEventStream((stream) -> stream.subscribe((ResponseStream e) -> e.accept(visitor)))
                            .onComplete(() -> System.out.println("\n\nCompleted streaming response."))
                            .onError((e) -> System.out.println("\n\nError: " + e.getMessage()))
                            .build();

            CompletableFuture<Void> futureResponse = bedrockRuntimeAsyncClient
                    .invokeModelWithResponseStream(request, responseHandler);

            futureResponse.join();

        } catch (BedrockRuntimeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;
    }
    // snippet-end:[bedrockruntime.java2.invoke_model_with_response_stream.main]
}
