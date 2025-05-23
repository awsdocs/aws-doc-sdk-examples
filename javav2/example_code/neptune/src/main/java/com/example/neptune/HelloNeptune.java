// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune;

import software.amazon.awssdk.core.async.SdkPublisher;
import software.amazon.awssdk.services.neptune.NeptuneAsyncClient;
import software.amazon.awssdk.services.neptune.model.DescribeDbClustersRequest;
import software.amazon.awssdk.services.neptune.model.DescribeDbClustersResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// snippet-start:[neptune.java2.hello.main]
/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class HelloNeptune {

    private static final String NEPTUNE_ENDPOINT = "https://[Specify-Your-Endpoint]:8182";

    public static void main(String[] args) {
        NeptuneAsyncClient neptuneClient = NeptuneAsyncClient.create();
        describeDbCluster(neptuneClient);
    }

    /**
     * Describes the Amazon Neptune DB clusters using a paginator.
     *
     * @param neptuneClient the Amazon Neptune asynchronous client
     */
    public static void describeDbCluster(NeptuneAsyncClient neptuneClient) {
        DescribeDbClustersRequest request = DescribeDbClustersRequest.builder()
                .maxRecords(20) // Optional: limit per page
                .build();

        SdkPublisher<DescribeDbClustersResponse> paginator= neptuneClient.describeDBClustersPaginator(request);
        CompletableFuture<Void> future = paginator
                .subscribe(response -> {
                    for (var cluster : response.dbClusters()) {
                        System.out.println("Cluster Identifier: " + cluster.dbClusterIdentifier());
                        System.out.println("Status: " + cluster.status());
                    }
                });

        // Wait for completion and handle errors
        try {
            future.get(); // Waits for all pages to be processed
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Failed to describe DB clusters: " + e.getMessage());
        } finally {
            neptuneClient.close();
        }
    }
}
// snippet-end:[neptune.java2.hello.main]