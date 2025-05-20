// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


package com.example.neptune.data;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunedata.model.ExecuteOpenCypherQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteOpenCypherQueryResponse;
import software.amazon.awssdk.services.neptunedata.model.NeptunedataException;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

/**
 * Example: Running OpenCypher queries on Amazon Neptune using AWS SDK for Java V2.
 *
 * ------------------------------------------------------------------------------
 * VPC NETWORKING REQUIREMENT:
 * ------------------------------------------------------------------------------
 * Amazon Neptune is accessible only within an Amazon VPC. This means:
 *
 * 1. This Java code must run within the same VPC as your Neptune cluster.
 *    Options include EC2, ECS, Lambda, AWS Cloud9, or a VPC-connected environment.
 *
 * 2. You must allow inbound access on port 8182 to Neptune from the host where this
 *    Java program runs (via the cluster's Security Group).
 *
 * 3. The endpoint used must be the Neptune HTTPS endpoint (e.g., https://your-neptune-endpoint:8182).
 *
 * To test network access from your environment:
 *     curl https://<your-neptune-endpoint>:8182/status
 * ------------------------------------------------------------------------------
 */
public class eptuneGremlinExplainAndProfileExample {

    // Specify the endpoint. You can obtain an endpoint by running
    // the main scenario.
    private static final String NEPTUNE_ENDPOINT = "https://<your-neptune-endpoint>:8182";

    public static void main(String[] args) {

        NeptunedataClient client = NeptunedataClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(NEPTUNE_ENDPOINT))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(10))
                        .socketTimeout(Duration.ofSeconds(30)))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(30))
                        .build())
                .build();

        try {
            runOpenCypherWithoutParameters(client);
            runOpenCypherWithParameters(client);
        } catch (NeptunedataException e) {
            System.err.println("Neptune error: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            client.close();
        }
    }

    private static void runOpenCypherWithoutParameters(NeptunedataClient client) {
        System.out.println("\nExecuting OpenCypher query WITHOUT parameters...");

        ExecuteOpenCypherQueryRequest request = ExecuteOpenCypherQueryRequest.builder()
                .openCypherQuery("MATCH (n {code: 'ANC'}) RETURN n")
                .build();

        ExecuteOpenCypherQueryResponse response = client.executeOpenCypherQuery(request);

        if (response.results() != null) {
            System.out.println("Results:");
            System.out.println(response.results());
        } else {
            System.out.println("No results returned.");
        }
    }

    private static void runOpenCypherWithParameters(NeptunedataClient client) {
        System.out.println("\nExecuting OpenCypher query WITH parameters...");

        ExecuteOpenCypherQueryRequest request = ExecuteOpenCypherQueryRequest.builder()
                .openCypherQuery("MATCH (n {code: $code}) RETURN n")
                .parameters(Map.of("code", "ANC").toString())
                .build();

        ExecuteOpenCypherQueryResponse response = client.executeOpenCypherQuery(request);

        if (response.results() != null) {
            System.out.println("Results:");
            System.out.println(response.results());
        } else {
            System.out.println("No results returned.");
        }
    }
}
