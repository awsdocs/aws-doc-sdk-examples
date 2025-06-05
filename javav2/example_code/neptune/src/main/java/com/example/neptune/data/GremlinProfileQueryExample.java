// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.data;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;


import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinProfileQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinProfileQueryResponse;
import software.amazon.awssdk.services.neptunedata.model.NeptunedataException;

import java.net.URI;
import java.time.Duration;

/**
 * Example: Running a Gremlin Profile query using the AWS SDK for Java V2.
 *
 * ----------------------------------------------------------------------------------
 * VPC Networking Requirement:
 * ----------------------------------------------------------------------------------
 * Amazon Neptune must be accessed from **within the same VPC** as the Neptune cluster.
 * It does not expose a public endpoint, so this code must be executed from:
 *   - An EC2 instance, Lambda function, ECS task, Cloud9 IDE, or
 *   - A connected environment (VPN, Direct Connect, or peered VPC).
 *
 * To see an example, see Creating an AWS Lambda function that queries Neptune graph data within the VPC
 * in the AWS Code Library.
 *
 * Notes:
 * - Ensure Neptune's security group allows inbound traffic on port 8182.
 * - Replace the endpoint below with your Neptune cluster's HTTPS endpoint.
 * - To verify access, run: curl https://<your-neptune-endpoint>:8182/status
 * ----------------------------------------------------------------------------------
 */
public class GremlinProfileQueryExample {

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
            executeGremlinProfileQuery(client);
        } catch (NeptunedataException e) {
            System.err.println("Neptune error: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            client.close();
        }
    }

    /**
     * Executes a Gremlin PROFILE query using the provided NeptunedataClient.
     *
     * @param client The NeptunedataClient instance to be used for executing the Gremlin PROFILE query.
     */
    private static void executeGremlinProfileQuery(NeptunedataClient client) {
        System.out.println("Executing Gremlin PROFILE query...");

        ExecuteGremlinProfileQueryRequest request = ExecuteGremlinProfileQueryRequest.builder()
                .gremlinQuery("g.V().has('code', 'ANC')")
                .build();

        ExecuteGremlinProfileQueryResponse response = client.executeGremlinProfileQuery(request);
        if (response.output() != null) {
            System.out.println("Query Profile Output:");
            System.out.println(response.output());
        } else {
            System.out.println("No output returned from the profile query.");
        }
    }
}