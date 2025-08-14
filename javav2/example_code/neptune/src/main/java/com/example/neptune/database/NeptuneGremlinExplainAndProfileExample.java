// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.database;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinExplainQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinExplainQueryResponse;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinProfileQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinProfileQueryResponse;
import software.amazon.awssdk.services.neptunedata.model.NeptunedataException;
import java.net.URI;
import java.time.Duration;

/**
 * This example demonstrates how to run a Gremlin Explain and Profile query on an Amazon Neptune database
 * using the AWS SDK for Java V2.
 *
 * VPC NETWORKING REQUIREMENT:
 * ----------------------------------------------------------------------
 * Amazon Neptune must be accessed from **within the same VPC** as the Neptune cluster.
 * It does not expose a public endpoint, so this code must be executed from:
 *
 *  - An **AWS Lambda function** configured to run inside the same VPC
 *  - An **EC2 instance** or **ECS task** running in the same VPC
 *  - A connected environment such as a **VPN**, **AWS Direct Connect**, or a **peered VPC**
 *
 * To see an example, see Creating an AWS Lambda function that queries Neptune graph data within the VPC
 * in the AWS Code Library.
 *
 */
public class NeptuneGremlinExplainAndProfileExample {
    // Specify the endpoint. You can obtain an endpoint by running
    // the main scenario.
     private static final String NEPTUNE_ENDPOINT = "https://[Specify-Your-Endpoint]:8182";

    public static void main(String[] args) {
        NeptunedataClient client = NeptunedataClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(NEPTUNE_ENDPOINT))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(10))
                        .socketTimeout(Duration.ofSeconds(30)))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(30))
                        .build())
                .build();

        executeGremlinExplainQuery(client);
    }

    /**
     * Executes a Gremlin explain query and a Gremlin profile query using the provided Neptune data client.
     *
     * @param client the Neptune data client to use for executing the Gremlin queries
     * @throws NeptunedataException if an error occurs while executing the Gremlin queries on the Neptune data client
     * @throws Exception if an unexpected error occurs during the execution
     */
    public static void executeGremlinExplainQuery(NeptunedataClient client) {
        try {
            runExplainQuery(client);
            runProfileQuery(client);
        } catch (NeptunedataException e) {
            System.err.println("Neptune error: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            client.close();
        }
    }

    /**
     * Runs an EXPLAIN query on the Neptune graph database using the provided NeptunedataClient.
     *
     * @param client The NeptunedataClient instance to use for executing the EXPLAIN query.
     */
    private static void runExplainQuery(NeptunedataClient client) {
        System.out.println("Running Gremlin EXPLAIN query...");
        ExecuteGremlinExplainQueryRequest explainRequest = ExecuteGremlinExplainQueryRequest.builder()
                .gremlinQuery("g.V().has('code', 'ANC')")
                .build();

        ExecuteGremlinExplainQueryResponse explainResponse = client.executeGremlinExplainQuery(explainRequest);

        System.out.println("Explain Query Result:");
        if (explainResponse.output() != null) {
            System.out.println(explainResponse.output());
        } else {
            System.out.println("No explain output returned.");
        }
    }

    /**
     * Runs a Gremlin PROFILE query using the provided NeptunedataClient instance.
     *
     * @param client the NeptunedataClient instance to use for executing the Gremlin query
     */
    private static void runProfileQuery(NeptunedataClient client) {
        System.out.println("Running Gremlin PROFILE query...");

        ExecuteGremlinProfileQueryRequest profileRequest = ExecuteGremlinProfileQueryRequest.builder()
                .gremlinQuery("g.V().has('code', 'ANC')")
                .build();

        ExecuteGremlinProfileQueryResponse profileResponse = client.executeGremlinProfileQuery(profileRequest);

        System.out.println("Profile Query Result:");
        if (profileResponse.output() != null) {
            System.out.println(profileResponse.output());
        } else {
            System.out.println("No profile output returned.");
        }
    }
}
