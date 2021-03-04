//snippet-sourcedescription:[ListAlgorithms.java demonstrates how to list algorithms.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon SageMaker]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[11/05/2020]
//snippet-sourceauthor:[scmacdon - AWS]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.example.sage;

//snippet-start:[sagemaker.java2.list_algs.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sagemaker.SageMakerClient;
import software.amazon.awssdk.services.sagemaker.model.ListAlgorithmsResponse;
import software.amazon.awssdk.services.sagemaker.model.AlgorithmSummary;
import software.amazon.awssdk.services.sagemaker.model.SageMakerException;
//snippet-end:[sagemaker.java2.list_algs.import]

import java.util.List;

public class ListAlgorithms {

    public static void main(String[] args) {

        Region region = Region.US_WEST_2;
        SageMakerClient sageMakerClient = SageMakerClient.builder()
                .region(region)
                .build();

        listAlgs(sageMakerClient);
        sageMakerClient.close();
    }

    //snippet-start:[sagemaker.java2.list_algs.main]
    public static void listAlgs(SageMakerClient sageMakerClient) {
        try {
            // Get a list of notebooks
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
    //snippet-end:[sagemaker.java2.list_algs.main]
}
