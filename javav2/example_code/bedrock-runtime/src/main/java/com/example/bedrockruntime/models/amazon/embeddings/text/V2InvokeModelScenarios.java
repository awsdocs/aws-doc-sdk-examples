// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockruntime.models.amazon.embeddings.text;

import com.example.bedrockruntime.libs.ScenarioRunner;
import org.json.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.io.IOException;

/**
 * This program demonstrates how to use InvokeModel with Amazon Titan Text Embeddings V2 on Amazon Bedrock.
 * <p>
 * For more examples in different programming languages check out the Amazon Bedrock User Guide at:
 * https://docs.aws.amazon.com/bedrock/latest/userguide/service_code_examples.html
 */

public class V2InvokeModelScenarios {
    // snippet-start:[bedrock-runtime.java2.InvokeModel_TitanTextEmbeddingsV2_AdditionalFields]

    /**
     * Invoke Amazon Titan Text Embeddings v2 with additional inference parameters.
     *
     * @param inputText  - The text to convert to an embedding.
     * @param dimensions - The number of dimensions the output embeddings should have.
     *                   Values accepted by the model: 256, 512, 1024.
     * @param normalize  - A flag indicating whether or not to normalize the output embeddings.
     * @return The {@link JSONObject} representing the model's response.
     */
    public static JSONObject invokeModel(String inputText, int dimensions, boolean normalize) {

        // Create a Bedrock Runtime client in the AWS Region of your choice.
        var client = BedrockRuntimeClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Set the model ID, e.g., Titan Embed Text v2.0.
        var modelId = "amazon.titan-embed-text-v2:0";

        // Create the request for the model.
        var nativeRequest = """
                {
                    "inputText": "%s",
                    "dimensions": %d,
                    "normalize": %b
                }
                """.formatted(inputText, dimensions, normalize);

        // Encode and send the request.
        var response = client.invokeModel(request -> {
            request.body(SdkBytes.fromUtf8String(nativeRequest));
            request.modelId(modelId);
        });

        // Decode the model's response.
        var modelResponse = new JSONObject(response.body().asUtf8String());

        // Extract and print the generated embedding and the input text token count.
        var embedding = modelResponse.getJSONArray("embedding");
        var inputTokenCount = modelResponse.getBigInteger("inputTextTokenCount");
        System.out.println("Embedding: " + embedding);
        System.out.println("\nInput token count: " + inputTokenCount);

        // Return the model's native response.
        return modelResponse;
    }
    // snippet-end:[bedrock-runtime.java2.InvokeModel_TitanTextEmbeddingsV2_AdditionalFields]

    public static void main(String[] args) throws IOException {
        new Demo().run();
    }

    private static class Demo {
        private final ScenarioRunner demo = new ScenarioRunner()
                .addScenario("Create an embedding with custom inference parameters");

        void run() throws IOException {
            demo.printHeader();

            var inputText = "Please recommend books with a theme similar to the movie 'Inception'.";
            var dimensions = 256;
            var normalize = true;
            var response = runTextScenario(inputText, dimensions, normalize);
            demo.printCurrentResponse(response);

            demo.printFooter();
        }

        private JSONObject runTextScenario(String inputText, int dimensions, boolean normalize) {
            demo.printScenarioHeader("Scenario - Create an embedding with custom inference parameters:");

            System.out.printf("%nInput text: '%s'%n", inputText);
            System.out.printf("Dimensions:   '%d'%n", dimensions);
            System.out.printf("Normalize:    '%b'%n%n", normalize);

            System.out.printf("Waiting for the response...%n");
            return invokeModel(inputText, dimensions, normalize);
        }
    }
}
