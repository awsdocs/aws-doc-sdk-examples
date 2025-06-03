// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.database;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.neptunedata.NeptunedataClient;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinQueryRequest;
import software.amazon.awssdk.services.neptunedata.model.ExecuteGremlinQueryResponse;
import software.amazon.awssdk.services.neptunedata.model.NeptunedataException;
import java.net.URI;
import java.time.Duration;

/**
 * This example demonstrates how to execute a Gremlin query on an Amazon Neptune database using the AWS SDK for Java V2.
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

public class NeptuneGremlinQueryExample {

    public static void main(String[] args) {
        // Specify the endpoint. You can obtain an endpoint by running
        // the main scenario.
        String neptuneEndpoint = "https://[Specify Endpoint]:8182";

        NeptunedataClient client = NeptunedataClient.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(neptuneEndpoint))
                .httpClientBuilder(ApacheHttpClient.builder()
                        .connectionTimeout(Duration.ofSeconds(10))
                        .socketTimeout(Duration.ofSeconds(30)))
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        .apiCallAttemptTimeout(Duration.ofSeconds(30))
                        .build())
                .build();
    }

    // snippet-start:[neptune.java2.data.query.gremlin.main]
    /**
     * Executes a Gremlin query against an Amazon Neptune database using the provided {@link NeptunedataClient}.
     *
     * @param client the {@link NeptunedataClient} instance to use for executing the Gremlin query
     */
    public static void executeGremlinQuery(NeptunedataClient client) {
        try {
            System.out.println("Querying Neptune...");
            ExecuteGremlinQueryRequest request = ExecuteGremlinQueryRequest.builder()
                    .gremlinQuery("g.V().has('code', 'ANC')")
                    .build();

            ExecuteGremlinQueryResponse response = client.executeGremlinQuery(request);

            System.out.println("Full Response:");
            System.out.println(response);

            // Retrieve and print the result
            if (response.result() != null) {
                System.out.println("Query Result:");
                System.out.println(response.result().toString());
            } else {
                System.out.println("No result returned from the query.");
            }
        } catch (NeptunedataException e) {
            System.err.println("Error calling Neptune: " + e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        } finally {
            client.close();
        }
    }
    // snippet-end:[neptune.java2.data.query.gremlin.main]
}
