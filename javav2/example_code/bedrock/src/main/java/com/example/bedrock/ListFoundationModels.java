// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[ListFoundationModels.java demonstrates how to obtain information about the available foundation models.]
// snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[AWS Bedrock]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.bedrock;

// snippet-start:[bedrock.java2.list_foundation_models.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrock.BedrockClient;
import software.amazon.awssdk.services.bedrock.model.BedrockException;
import software.amazon.awssdk.services.bedrock.model.FoundationModelSummary;
import software.amazon.awssdk.services.bedrock.model.ListFoundationModelsRequest;
import software.amazon.awssdk.services.bedrock.model.ListFoundationModelsResponse;

import java.util.List;
// snippet-end:[bedrock.java2.list_foundation_models.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListFoundationModels {
    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        BedrockClient bedrockClient = BedrockClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listAllFoundationModels(bedrockClient);
    }

    // snippet-start:[bedrock.java2.list_foundation_models.main]
    public static int listAllFoundationModels(BedrockClient bedrockClient) {

        try {
            ListFoundationModelsRequest request = ListFoundationModelsRequest.builder().build();

            ListFoundationModelsResponse response = bedrockClient.listFoundationModels(request);

            List<FoundationModelSummary> models = response.modelSummaries();

            for (FoundationModelSummary model : models) {
                System.out.println("Model ID: " + model.modelId());
                System.out.println("Provider: " + model.providerName());
                System.out.println("Name:     " + model.modelName());
                System.out.println();
            }

            return models.size();

        } catch (BedrockException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return 0;
    }
    // snippet-end:[bedrock.java2.list_foundation_models.main]
}
