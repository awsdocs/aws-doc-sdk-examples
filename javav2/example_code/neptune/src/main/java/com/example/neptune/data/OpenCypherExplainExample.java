// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.neptune.data;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
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
 * Amazon Neptune must be accessed from within an Amazon VPC. It does not expose
 * a public endpoint.
 *
 * 1. This Java application must run from an environment inside the same VPC as the Neptune cluster,
 *    such as EC2, ECS, AWS Lambda, AWS Cloud9, or a connected network via VPN or Direct Connect.
 *
 * 2. Ensure the Neptune cluster’s security group allows inbound access on port 8182 from the host
 *    running this application.
 *
 * 3. Use the Neptune HTTPS endpoint with port 8182 in `endpointOverride()`.
 *
 * Test connectivity with:
 *     curl https://<your-neptune-endpoint>:8182/status
 * ------------------------------------------------------------------------------
 */
public class OpenCypherExplainExample {

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
}
