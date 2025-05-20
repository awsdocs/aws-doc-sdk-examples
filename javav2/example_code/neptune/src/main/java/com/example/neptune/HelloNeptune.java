package com.example.neptune;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
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
 *  TIP:
 * You can test connectivity using `curl` or `telnet` from your instance to:
 *     curl https://<neptune-endpoint>:8182/status
 * If this fails, it’s likely a networking or security group issue.
 *
 * ----------------------------------------------------------------------
 */
public class HelloNeptune {

    private static final String NEPTUNE_ENDPOINT = "https://[Specify-Your-Endpoint]:8182";

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
