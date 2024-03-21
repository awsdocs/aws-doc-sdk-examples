// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[bedrock-runtime.java2.scenario_multiple_models.main]
package com.example.bedrockruntime;

import software.amazon.awssdk.services.bedrockruntime.model.BedrockRuntimeException;

import java.io.FileOutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Random;

import static com.example.bedrockruntime.InvokeModel.*;

/**
 * Demonstrates the invocation of the following models:
 * Anthropic Claude 2, AI21 Labs Jurassic-2, Meta Llama 2 Chat, and Stability.ai
 * Stable Diffusion XL.
 */
public class BedrockRuntimeUsageDemo {

    private static final Random random = new Random();

    private static final String CLAUDE = "anthropic.claude-v2";
    private static final String JURASSIC2 = "ai21.j2-mid-v1";
    private static final String LLAMA2 = "meta.llama2-13b-chat-v1";
    private static final String MISTRAL7B = "mistral.mistral-7b-instruct-v0:2";
    private static final String MIXTRAL8X7B = "mistral.mixtral-8x7b-instruct-v0:1";
    private static final String STABLE_DIFFUSION = "stability.stable-diffusion-xl";
    private static final String TITAN_IMAGE = "amazon.titan-image-generator-v1";

    public static void main(String[] args) {
        BedrockRuntimeUsageDemo.textToText();
        BedrockRuntimeUsageDemo.textToTextWithResponseStream();
        BedrockRuntimeUsageDemo.textToImage();
    }

    private static void textToText() {

        String prompt = "In one sentence, what is a large-language model?";
        BedrockRuntimeUsageDemo.invoke(CLAUDE, prompt);
        BedrockRuntimeUsageDemo.invoke(JURASSIC2, prompt);
        BedrockRuntimeUsageDemo.invoke(LLAMA2, prompt);
        BedrockRuntimeUsageDemo.invoke(MISTRAL7B, prompt);
        BedrockRuntimeUsageDemo.invoke(MIXTRAL8X7B, prompt);
    }

    private static void invoke(String modelId, String prompt) {
        invoke(modelId, prompt, null);
    }

    private static void invoke(String modelId, String prompt, String stylePreset) {
        System.out.println("\n" + new String(new char[88]).replace("\0", "-"));
        System.out.println("Invoking: " + modelId);
        System.out.println("Prompt: " + prompt);

        try {
            switch (modelId) {
                case CLAUDE:
                    printResponse(invokeClaude(prompt));
                    break;
                case JURASSIC2:
                    printResponse(invokeJurassic2(prompt));
                    break;
                case LLAMA2:
                    printResponse(invokeLlama2(prompt));
                    break;
                case MISTRAL7B:
                    for (String response : invokeMistral7B(prompt)) {
                        printResponse(response);
                    }
                    break;
                case MIXTRAL8X7B:
                    for (String response : invokeMixtral8x7B(prompt)) {
                        printResponse(response);
                    }
                    break;
                case STABLE_DIFFUSION:
                    createImage(STABLE_DIFFUSION, prompt, random.nextLong() & 0xFFFFFFFFL, stylePreset);
                    break;
                case TITAN_IMAGE:
                    createImage(TITAN_IMAGE, prompt, random.nextLong() & 0xFFFFFFFL);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + modelId);
            }
        } catch (BedrockRuntimeException e) {
            System.out.println("Couldn't invoke model " + modelId + ": " + e.getMessage());
            throw e;
        }
    }

    private static void createImage(String modelId, String prompt, long seed) {
        createImage(modelId, prompt, seed, null);
    }

    private static void createImage(String modelId, String prompt, long seed, String stylePreset) {
        String base64ImageData = (modelId.equals(STABLE_DIFFUSION))
                ? invokeStableDiffusion(prompt, seed, stylePreset)
                : invokeTitanImage(prompt, seed);
        String imagePath = saveImage(modelId, base64ImageData);
        System.out.printf("Success: The generated image has been saved to %s%n", imagePath);
    }

    private static void textToTextWithResponseStream() {
        String prompt = "What is a large-language model?";
        BedrockRuntimeUsageDemo.invokeWithResponseStream(CLAUDE, prompt);
    }

    private static void invokeWithResponseStream(String modelId, String prompt) {
        System.out.println(new String(new char[88]).replace("\0", "-"));
        System.out.printf("Invoking %s with response stream%n", modelId);
        System.out.println("Prompt: " + prompt);

        try {
            Claude2.invokeMessagesApiWithResponseStream(prompt);
        } catch (BedrockRuntimeException e) {
            System.out.println("Couldn't invoke model " + modelId + ": " + e.getMessage());
            throw e;
        }
    }

    private static void textToImage() {
        String imagePrompt = "stylized picture of a cute old steampunk robot";
        String stylePreset = "photographic";
        BedrockRuntimeUsageDemo.invoke(STABLE_DIFFUSION, imagePrompt, stylePreset);
        BedrockRuntimeUsageDemo.invoke(TITAN_IMAGE, imagePrompt);
    }

    private static void printResponse(String response) {
        System.out.printf("Generated text: %s%n", response);
    }

    private static String saveImage(String modelId, String base64ImageData) {
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
                fileName = String.format("%s_%d.png", modelId, i);
                i++;
            } while (Files.exists(outputPath.resolve(fileName)));

            byte[] imageBytes = Base64.getDecoder().decode(base64ImageData);

            Path filePath = outputPath.resolve(fileName);
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath.toFile())) {
                fileOutputStream.write(imageBytes);
            }

            return filePath.toString();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}
// snippet-end:[bedrock-runtime.java2.scenario_multiple_models.main]
