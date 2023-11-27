/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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
 * Anthropic Claude 2, AI21 Labs Jurassic-2, Meta Llama 2 Chat, and Stability.ai Stable Diffusion XL.
 */
public class BedrockRuntimeUsageDemo {

    private static final Random random = new Random();

    private static final String CLAUDE = "anthropic.claude-v2";
    private static final String JURASSIC2 = "ai21.j2-mid-v1";
    private static final String LLAMA2 = "meta.llama2-13b-chat-v1";
    private static final String STABLE_DIFFUSION = "stability.stable-diffusion-xl";

    public static void main(String[] args) {
        BedrockRuntimeUsageDemo.textToText();
        BedrockRuntimeUsageDemo.textToTextWithResponseStream();
        BedrockRuntimeUsageDemo.textToImage();
    }

    private static void textToText() {

        String prompt = "In one sentence, what is a large-language model?";
        BedrockRuntimeUsageDemo.invoke(CLAUDE, prompt, null);
        BedrockRuntimeUsageDemo.invoke(JURASSIC2, prompt, null);
        BedrockRuntimeUsageDemo.invoke(LLAMA2, prompt, null);
    }

    private static void invoke(String modelId, String prompt, String stylePreset) {
        System.out.println("\n" + new String(new char[88]).replace("\0", "-"));
        System.out.println("Invoking: " + modelId);
        System.out.println("Prompt: " + prompt);

        try {
            switch (modelId) {
                case CLAUDE:
                    printResponse(invokeClaude(prompt));
                    return;
                case JURASSIC2:
                    printResponse(invokeJurassic2(prompt));
                    return;
                case LLAMA2:
                    printResponse(invokeLlama2(prompt));
                    return;
                case STABLE_DIFFUSION:
                    long seed = (random.nextLong() & 0xFFFFFFFFL);
                    String base64ImageData = invokeStableDiffusion(prompt, seed, stylePreset);
                    String imagePath = saveImage(base64ImageData);
                    System.out.printf("Success: The generated image has been saved to %s%n", imagePath);
                    return;
                default:
                    throw new IllegalStateException("Unexpected value: " + modelId);
            }
        } catch (BedrockRuntimeException e) {
            System.out.println("Couldn't invoke model " + modelId + ": " + e.getMessage());
            throw e;
        }
    }

    private static void textToTextWithResponseStream() {
        String prompt = "What is a large-language model?";
        BedrockRuntimeUsageDemo.invoke(CLAUDE, prompt);
    }

    private static void invoke(String modelId, String prompt) {
        System.out.println(new String(new char[88]).replace("\0", "-"));
        System.out.printf("Invoking %s with response stream%n", modelId);
        System.out.println("Prompt: " + prompt);

        try {
            var silent = false;
            InvokeModelWithResponseStream.invokeClaude(prompt, silent);
        } catch (BedrockRuntimeException e) {
            System.out.println("Couldn't invoke model " + modelId + ": " + e.getMessage());
            throw e;
        }
    }

    private static void textToImage() {
        String imagePrompt = "A sunset over the ocean";
        String stylePreset = "photographic";
        BedrockRuntimeUsageDemo.invoke(STABLE_DIFFUSION, imagePrompt, stylePreset);
    }

    private static void printResponse(String response) {
        System.out.printf("Generated text: %s%n", response);
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
