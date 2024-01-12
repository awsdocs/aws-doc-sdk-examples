// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sage;

// snippet-start:[sagemaker.java2.list_algs.main]
// snippet-start:[sagemaker.java2.list_algs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ListAlgorithmsResponse;
import software.amazon.awssdk.services.sagemaker.model.AlgorithmSummary;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
import java.util.List;
// snippet-end:[sagemaker.java2.list_algs.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListAlgorithms {
    public static void main(String[] args) {
        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
                .build();

        listAlgs(sageMakerClient);
        sageMakerClient.close();
    }

    public static void listAlgs(SageMakerClient sageMakerClient) {
        try {
            ListAlgorithmsResponse algorithmsResponse = sageMakerClient.listAlgorithms();
            List<AlgorithmSummary> items = algorithmsResponse.algorithmSummaryList();
            for (AlgorithmSummary item : items) {
                System.out.println("Algorithm name is: " + item.algorithmName());
            }

        } catch (SageMakerException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}
// snippet-end:[sagemaker.java2.list_algs.main]
