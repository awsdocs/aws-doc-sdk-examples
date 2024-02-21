// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrock.sync;

// snippet-start:[bedrock.java2.list_foundation_models.import]
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.FoundationModelSummary;
import software.amazon.awssdk.services.bedrock.model.ListFoundationModelsResponse;

import java.util.List;
// snippet-end:[bedrock.java2.list_foundation_models.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListFoundationModels {

    private static Region region;

    public static void main(String[] args) {
        final String usage = """

                Usage:
                    [<region>]\s

                Where:
                    region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'.
                """;

        if (args.length > 1) {
            System.out.println(usage);
            System.exit(1);
        }

        region = args.length == 1 ? Region.of(args[0]) : Region.US_EAST_1;

        System.out.println("Initializing the Amazon Bedrock client...");
        System.out.printf("Region: %s%n", region.toString());

        BedrockClient bedrockClient = BedrockClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(region)
                .build();

        listFoundationModels(bedrockClient);
    }

    // snippet-start:[bedrock.java2.list_foundation_models.main]
    /**
     * Lists Amazon Bedrock foundation models that you can use.
     * You can filter the results with the request parameters.
     *
     * @param bedrockClient The service client for accessing Amazon Bedrock.
     * @return A list of objects containing the foundation models' details
     */
    public static List<FoundationModelSummary> listFoundationModels(BedrockClient bedrockClient) {

        try {
            ListFoundationModelsResponse response = bedrockClient.listFoundationModels(r -> {});

            List<FoundationModelSummary> models = response.modelSummaries();

            if (models.isEmpty()) {
                System.out.println("No available foundation models in " + region.toString());
            } else {
                for (FoundationModelSummary model : models) {
                    System.out.println("Model ID: " + model.modelId());
                    System.out.println("Provider: " + model.providerName());
                    System.out.println("Name:     " + model.modelName());
                    System.out.println();
                }
            }

            return models;

        } catch (SdkClientException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // snippet-end:[bedrock.java2.list_foundation_models.main]
}
