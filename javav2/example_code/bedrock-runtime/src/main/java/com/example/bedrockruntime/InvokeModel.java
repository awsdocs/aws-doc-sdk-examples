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

import org.json.JSONArray;
import org.json.JSONObject;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.Random;
// snippet-end:[bedrock-runtime.java2.invoke_model.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class InvokeModel {

    private static final Random random = new Random();

    public static void main(String[] args) {
        usageDemo();
    }

    // snippet-start:[bedrock-runtime.java2.invoke_claude.main]
    /**
     * Invokes the Anthropic Claude 2 model to run an inference based on the provided input.
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
     * Invokes the AI21 Labs Jurassic-2 model to run an inference based on the provided input.
     *
     * @param client A Bedrock Runtime client
     * @param prompt The prompt that you want Jurassic to complete.
     * @return Inference response from the model.
     */
    public static String invokeJurassic2(BedrockRuntimeClient client, String prompt) {

        try {

            /*
              For request/response formats, defaults, and value ranges of AI21 Labs Jurassic-2, see:
              https://docs.ai21.com/reference/j2-complete-ref
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

    // snippet-start:[bedrock-runtime.java2.invoke_llama2.main]
    /**
     * Invokes the Meta Llama 2 Chat model to run an inference based on the provided input.
     *
     * @param client A Bedrock Runtime client
     * @param prompt The prompt that you want Llama 2 to complete.
     * @return Inference response from the model.
     */
    public static String invokeLlama2(BedrockRuntimeClient client, String prompt) {

        try {

            /*
              For request/response formats, defaults, and value ranges of Meta Llama 2 Chat, see:
              https://docs.aws.amazon.com/bedrock/latest/userguide/model-parameters-meta.html
             */

            JSONObject payload = new JSONObject()
                    .put("prompt", prompt)
                    .put("max_gen_len", 512)
                    .put("temperature", 0.5)
                    .put("top_p", 0.9);

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("meta.llama2-13b-chat-v1")
                    .body(body)
                    .build();

            InvokeModelResponse response = client.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());

            String completion = responseBody.getString("generation");

            return completion;

        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;

    }
    // snippet-end:[bedrock-runtime.java2.invoke_llama2.main]

    // snippet-start:[bedrock-runtime.java2.invoke_stable_diffusion.main]
    /**
     * Invokes the Stability.ai Stable Diffusion XL model to create an image based on the provided input.
     *
     * @param client A Bedrock Runtime client
     * @param prompt The prompt that guides the Stable Diffusion model.
     * @param seed The random noise seed for image generation (use 0 or omit for a random seed).
     * @param stylePreset The style preset to guide the image model towards a specific style.
     * @return A Base64-encoded string representing the model's inference response as an image.
     */
    public static String invokeStableDiffusion(BedrockRuntimeClient client, String prompt, long seed, String stylePreset) {

        try {

            /*
             For request/response formats, defaults, and value ranges of Stable Diffusion, see:
             https://platform.stability.ai/docs/api-reference#tag/v1generation
             */

            JSONObject payload = new JSONObject()
                    .put("text_prompts",
                            new JSONArray()
                                    .put(new JSONObject().put("text", prompt))
                    )
                    .put("seed", seed);

            if (stylePreset != null && !stylePreset.isEmpty()) {
                payload.put("style_preset", stylePreset);
            }

            SdkBytes body = SdkBytes.fromUtf8String(payload.toString());

            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId("stability.stable-diffusion-xl")
                    .body(body)
                    .build();

            InvokeModelResponse response = client.invokeModel(request);

            JSONObject responseBody = new JSONObject(response.body().asUtf8String());

            String base64ImageData = responseBody
                    .getJSONArray("artifacts")
                    .getJSONObject(0)
                    .getString("base64");

            return base64ImageData;

        } catch (AwsServiceException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return null;

    }
    // snippet-end:[bedrock-runtime.java2.invoke_stable_diffusion.main]


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
        invoke(client, "meta.llama2-13b-chat-v1", textPrompt);

        String imagePrompt = "A sunset over the ocean";
        String stylePreset = "photographic";
        invoke(client, "stability.stable-diffusion-xl", imagePrompt, stylePreset);
    }

    private static void invoke(BedrockRuntimeClient client, String modelId, String prompt) {
        invoke(client, modelId, prompt, null);
    }

    private static void invoke(BedrockRuntimeClient client, String modelId, String prompt, String stylePreset) {
        System.out.println(new String(new char[88]).replace("\0", "-"));
        System.out.println("Invoking: " + modelId);
        System.out.println("Prompt: " + prompt);

        try {
            String completion;
            if ("anthropic.claude-v2".equals(modelId)) {
                completion = invokeClaude(client, prompt);
                System.out.printf("Completion: %s%n", completion);
            } else if ("ai21.j2-mid-v1".equals(modelId)) {
                completion = invokeJurassic2(client, prompt);
                System.out.printf("Completion: %s%n", completion);
            } else if ("meta.llama2-13b-chat-v1".equals(modelId)) {
                completion = invokeLlama2(client, prompt);
                System.out.printf("Completion: %s%n", completion);
            } else if ("stability.stable-diffusion-xl".equals(modelId)) {
                long seed = (random.nextLong() & 0xFFFFFFFFL);
                String base64ImageData = invokeStableDiffusion(client, prompt, seed, stylePreset);
                String imagePath = saveImage(base64ImageData);
                System.out.printf("The generated image has been saved to %s%n", imagePath);
            }
        } catch (BedrockRuntimeException e) {
            System.out.println("Couldn't invoke model " + modelId + ": " + e.getMessage());
            throw e;
        }
    }

    private static String saveImage(String base64ImageData) {
        try {

            String directory = "output";

            URI uri = InvokeModel.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            Path outputPath = Paths.get(uri).getParent().getParent().resolve(directory);

            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }

            int i = 1;
            String fileName;
            do {
                fileName = String.format("image_%d.png", i);
                i++;
            } while (Files.exists(outputPath.resolve(fileName)));

            byte[] imageBytes = Base64.getDecoder().decode(base64ImageData);

            Path filePath = outputPath.resolve(fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                fileOutputStream.write(imageBytes);
            }

            return filePath.toString();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
