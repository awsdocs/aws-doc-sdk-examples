// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.analytics;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunegraph.NeptuneGraphClient;
import software.amazon.awssdk.services.neptunegraph.model.ExecuteQueryRequest;
import software.amazon.awssdk.services.neptunegraph.model.ExecuteQueryResponse;
import software.amazon.awssdk.services.neptunegraph.model.NeptuneGraphException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

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

public class NeptuneAnalyticsQueryExample {

    public static void main(String[] args) {

        // Replace with your Neptune Analytics graph endpoint (including port 8182)
        // You can get the Endpoint value by running CreateNeptuneGraphExample
        String neptuneAnalyticsEndpoint = "https://<your-neptune-analytics-endpoint>:8182";
        String graphId = "<your-graph-id>";

        NeptuneGraphClient client = NeptuneGraphClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(neptuneAnalyticsEndpoint))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(10))
                        .socketTimeout(Duration.ofSeconds(0)) // No socket timeout (read_timeout=None)
                )
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(0)) // No total timeout
                        .retryPolicy(b -> b.numRetries(0)) // Disable retries (total_max_attempts=1)
                        .build())
                .build();

        executeGremlinProfileQuery(client, graphId);
    }

    // snippet-start:[neptune.java2.graph.execute.main]
    /**
     * Executes a Gremlin profile query on the Neptune Analytics graph.
     *
     * @param client       the {@link NeptuneGraphClient} instance to use for the query
     * @param graphId      the identifier of the graph to execute the query on
     *
     * @throws NeptuneGraphException if an error occurs while executing the query on the Neptune Graph
     * @throws Exception if an unexpected error occurs
     */
    public static void executeGremlinProfileQuery(NeptuneGraphClient client, String graphId) {

        try {
            System.out.println("Running openCypher query on Neptune Analytics...");

            ExecuteQueryRequest request = ExecuteQueryRequest.builder()
                    .graphIdentifier(graphId)
                    .queryString("MATCH (n {code: 'ANC'}) RETURN n")
                    .language("OPEN_CYPHER")
                    .build();

            ResponseInputStream<ExecuteQueryResponse> response = client.executeQuery(request);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(response, StandardCharsets.UTF_8))) {
                String result = reader.lines().collect(Collectors.joining("\n"));
                System.out.println("Query Result:");
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("Error reading response: " + e.getMessage());
            }

        } catch (NeptuneGraphException e) {
            System.err.println("NeptuneGraph error: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    // snippet-end:[neptune.java2.graph.execute.main]
}

