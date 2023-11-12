// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[InvokeModel.java demonstrates how to invoke a model with Amazon Bedrock.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Bedrock]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.bedrockruntime;

// snippet-start:[bedrock-runtime.java2.invoke_model.import]
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.List;
// snippet-end:[bedrock-runtime.java2.invoke_model.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InvokeModel {

    public static void main(String[] args) {
        usageDemo();
    }

    /**
     * Demonstrates the invocation of various large-language and image generation models:
     * Anthropic Claude 2, AI21 Labs Jurassic-2, and Stability.ai Stable Diffusion XL.
     */
    private static void usageDemo() {
        Region region = Region.US_EAST_1;
        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        System.out.println(String.format("%0" + 88 + "d", 0).replace("0", "-"));
        System.out.println("Welcome to the Amazon Bedrock Runtime demo.");

        String textPrompt = "In one sentence, what is a large-language model?";
        invoke(client, "anthropic.claude-v2", textPrompt);
        invoke(client, "ai21.j2-mid-v1", textPrompt);
    }

    // snippet-start:[bedrock-runtime.java2.invoke_claude.main]
    /**
     * Invokes the Anthropic Claude 2 model to run an inference using the input
     * provided in the request body.
     *
     * @param client A Bedrock Runtime client
     * @param prompt The prompt that you want Claude to complete.
     * @return Inference response from the model.
     */
    public static String invokeClaude(BedrockRuntimeClient client, String prompt) {

        try {

            /*
              For request/response formats, defaults, and value ranges of Anthropic Claude, see:
              https://docs.anthropic.com/claude/reference/complete_post
             */

            // Claude requires you to enclose the prompt as follows:
            String enclosedPrompt = "Human: " + prompt + "\n\nAssistant:";

            JSONObject payload = new JSONObject()
                    .put("prompt", "Human: " + enclosedPrompt + " Assistant:")
                    .put("max_tokens_to_sample", 200)
                    .put("temperature", 0.5)
                    .put("stop_sequences", List.of("\n\nHuman:"));

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("anthropic.claude-v2")
                    .body(body)
                    .build();

            InvokeModelResponse response = client.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());

            String completion = responseBody.getString("completion");

            return completion;

        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;

    }
    // snippet-end:[bedrock-runtime.java2.invoke_claude.main]

    // snippet-start:[bedrock-runtime.java2.invoke_jurassic2.main]
    /**
     * Invokes the AI21 Labs Jurassic-2 model to run an inference using the input
     * provided in the request body.
     *
     * @param client A Bedrock Runtime client
     * @param prompt The prompt that you want Jurassic to complete.
     * @return Inference response from the model.
     */
    public static String invokeJurassic2(BedrockRuntimeClient client, String prompt) {

        try {

            /*
              For request/response formats, defaults, and value ranges of AI21 Labs Jurassic-2, see:
              https://docs.anthropic.com/claude/reference/complete_post
             */

            JSONObject payload = new JSONObject()
                    .put("prompt", prompt)
                    .put("maxTokens", 200)
                    .put("temperature", 0.5);

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("ai21.j2-mid-v1")
                    .body(body)
                    .build();

            InvokeModelResponse response = client.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());

            String completion = responseBody
                    .getJSONArray("completions")
                    .getJSONObject(0)
                    .getJSONObject("data")
                    .getString("text");

            return completion;

        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;

    }
    // snippet-end:[bedrock-runtime.java2.invoke_jurassic2.main]


    public static void invoke(BedrockRuntimeClient client, String modelId, String prompt) {
        System.out.println(new String(new char[88]).replace("\0", "-"));
        System.out.println("Invoking: " + modelId);
        System.out.println("Prompt: " + prompt);

        try {
            String completion;
            if ("anthropic.claude-v2".equals(modelId)) {
                completion = invokeClaude(client, prompt);
                System.out.println("Completion: " + completion.trim());
            } else if ("ai21.j2-mid-v1".equals(modelId)) {
                completion = invokeJurassic2(client, prompt);
                System.out.println("Completion: " + completion.trim());
            }
        } catch (BedrockRuntimeException e) {
            System.out.println("Couldn't invoke model " + modelId + ": " + e.getMessage());
            throw e;
        }
    }
}
