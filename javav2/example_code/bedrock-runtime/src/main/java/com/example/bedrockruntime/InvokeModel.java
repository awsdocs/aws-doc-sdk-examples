// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[InvokeModel.java demonstrates how to invoke models with Amazon Bedrock.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Amazon Bedrock]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.bedrockruntime;

// snippet-start:[bedrock-runtime.java2.invoke_model.import]

import org.json.JSONArray;
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
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

    // snippet-start:[bedrock-runtime.java2.invoke_claude.main]
    /**
     * Invokes the Anthropic Claude 2 model to run an inference based on the provided input.
     *
     * @param prompt The prompt for Claude to complete.
     * @return The generated response.
     */
    public static String invokeClaude(String prompt) {
        /*
          The different model providers have individual request and response formats.
          For the format, ranges, and default values for Anthropic Claude, refer to:
          https://docs.anthropic.com/claude/reference/complete_post
         */

        String claudeModelId = "anthropic.claude-v2";

        // Claude requires you to enclose the prompt as follows:
        String enclosedPrompt = "Human: " + prompt + "\n\nAssistant:";

        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String payload = new JSONObject()
                .put("prompt", enclosedPrompt)
                .put("max_tokens_to_sample", 200)
                .put("temperature", 0.5)
                .put("stop_sequences", List.of("\n\nHuman:"))
                .toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload))
                .modelId(claudeModelId)
                .contentType("application/json")
                .accept("application/json")
                .build();


        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());

        String generatedText = responseBody.getString("completion");

        return generatedText;
    }
    // snippet-end:[bedrock-runtime.java2.invoke_claude.main]

    // snippet-start:[bedrock-runtime.java2.invoke_jurassic2.main]
    /**
     * Invokes the AI21 Labs Jurassic-2 model to run an inference based on the provided input.
     *
     * @param prompt The prompt for Jurassic to complete.
     * @return The generated response.
     */
    public static String invokeJurassic2(String prompt) {
        /*
          The different model providers have individual request and response formats.
          For the format, ranges, and default values for AI21 Labs Jurassic-2, refer to:
          https://docs.ai21.com/reference/j2-complete-ref
         */

        String jurassic2ModelId = "ai21.j2-mid-v1";

        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String payload = new JSONObject()
                .put("prompt", prompt)
                .put("temperature", 0.5)
                .put("maxTokens", 200)
                .toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload))
                .modelId(jurassic2ModelId)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());

        String generatedText = responseBody
                .getJSONArray("completions")
                .getJSONObject(0)
                .getJSONObject("data")
                .getString("text");

        return generatedText;
    }
    // snippet-end:[bedrock-runtime.java2.invoke_jurassic2.main]

    // snippet-start:[bedrock-runtime.java2.invoke_llama2.main]
    /**
     * Invokes the Meta Llama 2 Chat model to run an inference based on the provided input.
     *
     * @param prompt The prompt for Llama 2 to complete.
     * @return The generated response.
     */
    public static String invokeLlama2(String prompt) {
        /*
          The different model providers have individual request and response formats.
          For the format, ranges, and default values for Meta Llama 2 Chat, refer to:
          https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html
         */

        String llama2ModelId = "meta.llama2-13b-chat-v1";

        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String payload = new JSONObject()
                .put("prompt", prompt)
                .put("max_gen_len", 512)
                .put("temperature", 0.5)
                .put("top_p", 0.9)
                .toString();

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload))
                .modelId(llama2ModelId)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());

        String generatedText = responseBody.getString("generation");

        return generatedText;
    }
    // snippet-end:[bedrock-runtime.java2.invoke_llama2.main]

    // snippet-start:[bedrock-runtime.java2.invoke_stable_diffusion.main]
    /**
     * Invokes the Stability.ai Stable Diffusion XL model to create an image based on the provided input.
     *
     * @param prompt The prompt that guides the Stable Diffusion model.
     * @param seed The random noise seed for image generation (use 0 or omit for a random seed).
     * @param stylePreset The style preset to guide the image model towards a specific style.
     * @return A Base64-encoded string representing the generated image.
     */
    public static String invokeStableDiffusion(String prompt, long seed, String stylePreset) {
        /*
          The different model providers have individual request and response formats.
          For the format, ranges, and available style_presets of Stable Diffusion models refer to:
          https://platform.stability.ai/docs/api-reference#tag/v1generation
         */

        String stableDiffusionModelId = "stability.stable-diffusion-xl";

        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        JSONArray wrappedPrompt = new JSONArray().put(new JSONObject().put("text", prompt));

        JSONObject payload = new JSONObject()
                .put("text_prompts", wrappedPrompt)
                .put("seed", seed);

        if (!(stylePreset == null || stylePreset.isEmpty())) {
            payload.put("style_preset", stylePreset);
        }

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload.toString()))
                .modelId(stableDiffusionModelId)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());

        String base64ImageData = responseBody
                .getJSONArray("artifacts")
                .getJSONObject(0)
                .getString("base64");

        return base64ImageData;
    }
    // snippet-end:[bedrock-runtime.java2.invoke_stable_diffusion.main]

    // snippet-start:[bedrock-runtime.java2.invoke_titan_image.main]
    /**
     * Invokes the Amazon Titan image generation model to create an image using the input
     * provided in the request body.
     *
     * @param prompt The prompt that you want Amazon Titan to use for image generation.
     * @param seed The random noise seed for image generation (Range: 0 to 2147483647).
     * @return A Base64-encoded string representing the generated image.
     */
    public static String invokeTitanImage(String prompt, long seed) {
        /*
         The different model providers have individual request and response formats.
         For the format, ranges, and default values for Titan Image models refer to:
         https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-titan-image.html
        */
        String titanImageModelId = "amazon.titan-image-generator-v1";

        BedrockRuntimeClient client = BedrockRuntimeClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        var textToImageParams = new JSONObject().put("text", prompt);

        var imageGenerationConfig = new JSONObject()
                .put("numberOfImages", 1)
                .put("quality", "standard")
                .put("cfgScale", 8.0)
                .put("height", 512)
                .put("width", 512)
                .put("seed", seed);

        JSONObject payload = new JSONObject()
                .put("taskType", "TEXT_IMAGE")
                .put("textToImageParams", textToImageParams)
                .put("imageGenerationConfig", imageGenerationConfig);

        InvokeModelRequest request = InvokeModelRequest.builder()
                .body(SdkBytes.fromUtf8String(payload.toString()))
                .modelId(titanImageModelId)
                .contentType("application/json")
                .accept("application/json")
                .build();

        InvokeModelResponse response = client.invokeModel(request);

        JSONObject responseBody = new JSONObject(response.body().asUtf8String());

        String base64ImageData = responseBody
                .getJSONArray("images")
                .getString(0);

        return base64ImageData;
    }
    // snippet-end:[bedrock-runtime.java2.invoke_titan_image.main]
}
