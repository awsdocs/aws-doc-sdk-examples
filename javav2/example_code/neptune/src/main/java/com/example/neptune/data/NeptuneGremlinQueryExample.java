// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.data;

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
 * Amazon Neptune is designed to be **accessed from within an Amazon VPC**.
 * It does not expose a public endpoint. This means:
 *
 * 1. Your Java application must run **within the same VPC** (e.g., via an EC2 instance, Lambda function, ECS task,
 *    or AWS Cloud9 environment), or from a peered VPC that has network access to Neptune.
 *
 * 2. You cannot run this example directly from your local machine (e.g., via IntelliJ or PyCharm on your laptop)
 *    unless you set up a VPN or AWS Direct Connect that bridges your local environment to your VPC.
 *
 * 3. You must ensure the **VPC Security Group** attached to your Neptune cluster allows **inbound access on port 8182**
 *    from the instance or environment where this Java code runs.
 *
 * 4. The `endpointOverride()` must use the **HTTPS Neptune endpoint** including the `:8182` port.
 *
 * To see an example, see Creating an AWS Lambda function that queries Neptune graph data within the VPC
 * in the AWS Code Library.
 *
 *  TIP:
 * You can test connectivity using `curl` or `telnet` from your instance to:
 *     curl https://<neptune-endpoint>:8182/status
 * If this fails, it’s likely a networking or security group issue.
 *
 * ----------------------------------------------------------------------
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
}
