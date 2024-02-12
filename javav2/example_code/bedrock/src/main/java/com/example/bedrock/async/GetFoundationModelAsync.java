// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrock.async;

// snippet-start:[bedrock.java2.get_foundation_model_async.import]
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockAsyncClient;
import software.amazon.awssdk.services.bedrock.model.FoundationModelDetails;
import software.amazon.awssdk.services.bedrock.model.GetFoundationModelResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
// snippet-end:[bedrock.java2.get_foundation_model_async.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetFoundationModelAsync {
    public static void main(String[] args) {
        final String usage = """

                Usage:
                    <modelId> [<region>]\s

                Where:
                    modelId - The ID of the foundation model you want to use.
                    region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'.
                """;

        if (args.length < 1 || args.length > 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String modelId = args[0];
        Region region = args.length == 2 ? Region.of(args[1]) : Region.US_EAST_1;

        System.out.println("Initializing the Amazon Bedrock async client...");
        System.out.printf("Region: %s%n", region.toString());

        BedrockAsyncClient client = BedrockAsyncClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .build();

        getFoundationModel(client, modelId);
    }

    // snippet-start:[bedrock.java2.get_foundation_model_async.main]
    /**
     * Get details about an Amazon Bedrock foundation model.
     *
     * @param bedrockClient   The async service client for accessing Amazon Bedrock.
     * @param modelIdentifier The model identifier.
     * @return An object containing the foundation model's details.
     */
    public static FoundationModelDetails getFoundationModel(BedrockAsyncClient bedrockClient, String modelIdentifier) {
        try {
            CompletableFuture<GetFoundationModelResponse> future = bedrockClient.getFoundationModel(
                    r -> r.modelIdentifier(modelIdentifier)
            );

            FoundationModelDetails model = future.get().modelDetails();

            System.out.println(" Model ID:                     " + model.modelId());
            System.out.println(" Model ARN:                    " + model.modelArn());
            System.out.println(" Model Name:                   " + model.modelName());
            System.out.println(" Provider Name:                " + model.providerName());
            System.out.println(" Lifecycle status:             " + model.modelLifecycle().statusAsString());
            System.out.println(" Input modalities:             " + model.inputModalities());
            System.out.println(" Output modalities:            " + model.outputModalities());
            System.out.println(" Supported customizations:     " + model.customizationsSupported());
            System.out.println(" Supported inference types:    " + model.inferenceTypesSupported());
            System.out.println(" Response streaming supported: " + model.responseStreamingSupported());

            return model;

        } catch (ExecutionException e) {
            if (e.getMessage().contains("ValidationException")) {
                throw new IllegalArgumentException(e.getMessage());
            } else {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // snippet-end:[bedrock.java2.get_foundation_model_async.main]
}
