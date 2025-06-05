// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.analytics;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunegraph.NeptuneGraphClient;
import software.amazon.awssdk.services.neptunegraph.model.CreateGraphRequest;
import software.amazon.awssdk.services.neptunegraph.model.CreateGraphResponse;
import software.amazon.awssdk.services.neptunegraph.model.NeptuneGraphException;

/**
 * This Java example demonstrates how to query Amazon Neptune Analytics (Neptune Graph) using the AWS SDK for Java V2.
 *
 * VPC NETWORKING REQUIREMENT:
 * ----------------------------------------------------------------------
 * Amazon Neptune Analytics must be accessed from within an Amazon VPC. This means:
 *
 * 1. Your application must run within a VPC environment such as EC2, Lambda, ECS, Cloud9, or an AWS managed notebook.
 * 2. You **cannot run this code from your local machine** unless you are connected via a VPN or Direct Connect.
 * 3. Ensure that your Neptune Graph cluster endpoint is accessible and security groups allow inbound access from your client.
 * 4. Always use the HTTPS endpoint when setting the `endpointOverride()` value.
 *
 * You can test access by running:
 *     curl https://<graph-endpoint>:8182/status
 * ----------------------------------------------------------------------
 */

public class CreateNeptuneGraphExample {

    public static void main(String[] args) {
        Region region = Region.US_EAST_1;
        String graphName = "sample-analytics-graph";

        // Create the NeptuneGraph client
        NeptuneGraphClient client = NeptuneGraphClient.builder()
                .region(region)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();

        executeCreateGraph(client, graphName);
    }

    // snippet-start:[neptune.java2.graph.create.main]
    /**
     * Executes the process of creating a new Neptune graph.
     *
     * @param client        the Neptune graph client used to interact with the Neptune service
     * @param graphName     the name of the graph to be created
     * @throws NeptuneGraphException if an error occurs while creating the graph
     */
    public static void executeCreateGraph(NeptuneGraphClient client, String graphName) {
        try {
            // Create the graph request
            CreateGraphRequest request = CreateGraphRequest.builder()
                    .graphName(graphName)
                    .provisionedMemory(16)
                    .build();

            // Create the graph
            CreateGraphResponse response = client.createGraph(request);

            // Extract the graph name and ARN
            String createdGraphName = response.name();
            String graphArn = response.arn();
            String graphEndpoint = response.endpoint();

            System.out.println("Graph created successfully!");
            System.out.println("Graph Name: " + createdGraphName);
            System.out.println("Graph ARN: " + graphArn);
            System.out.println("Graph Endpoint: " +graphEndpoint );

        } catch (NeptuneGraphException e) {
            System.err.println("Failed to create graph: " + e.awsErrorDetails().errorMessage());
        } finally {
            client.close();
        }
   }
    // snippet-end:[neptune.java2.graph.create.main]
}

