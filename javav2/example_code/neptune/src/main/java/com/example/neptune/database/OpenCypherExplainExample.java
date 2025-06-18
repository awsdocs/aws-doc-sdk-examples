// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.database;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunedata.model.ExecuteOpenCypherExplainQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteOpenCypherExplainQueryResponse;
import software.amazon.awssdk.services.neptunedata.model.NeptunedataException;
import java.net.URI;
import java.time.Duration;

/**
 * Example: Running an OpenCypher EXPLAIN query on Amazon Neptune using AWS SDK for Java V2.
 *
 * ------------------------------------------------------------------------------
 * VPC NETWORKING REQUIREMENT:
 * ------------------------------------------------------------------------------
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
public class OpenCypherExplainExample {

    private static final String NEPTUNE_ENDPOINT = "https://<your-neptune-endpoint>:8182";

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

        executeGremlinQuery(client);
    }

    // snippet-start:[neptune.java2.data.query.opencypher.main]
    /**
     * Executes an OpenCypher EXPLAIN query using the provided Neptune data client.
     *
     * @param client The Neptune data client to use for the query execution.
     */
    public static void executeGremlinQuery(NeptunedataClient client) {
        try {
            System.out.println("Executing OpenCypher EXPLAIN query...");
            ExecuteOpenCypherExplainQueryRequest request = ExecuteOpenCypherExplainQueryRequest.builder()
                    .openCypherQuery("MATCH (n {code: 'ANC'}) RETURN n")
                    .explainMode("debug")
                    .build();

            ExecuteOpenCypherExplainQueryResponse response = client.executeOpenCypherExplainQuery(request);

            if (response.results() != null) {
                System.out.println("Explain Results:");
                System.out.println(response.results().asUtf8String());
            } else {
                System.out.println("No explain results returned.");
            }

        } catch (NeptunedataException e) {
            System.err.println("Neptune error: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    // snippet-end:[neptune.java2.data.query.opencypher.main]
}
