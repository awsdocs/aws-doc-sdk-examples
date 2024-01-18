// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockagent.async;

// snippet-start:[bedrock-agent.java2.list_agents_async.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentAsyncClient;
import software.amazon.awssdk.services.bedrockagent.model.AgentSummary;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentsRequest;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentsResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
// snippet-end:[bedrock-agent.java2.list_agents_async.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListAgentsAsync {
    public static void main(String[] args) {
        final String usage = """
            
            Usage:
                [<region>]\s
                
            Where:
                region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'
        """;

        if (args.length > 1) {
            System.out.println(usage);
            System.exit(1);
        }

        Region region = args.length == 1 ? Region.of(args[0]) : Region.US_EAST_1;

        System.out.println("Initializing the Amazon Bedrock Agent Client...");
        System.out.printf("Region: %s%n", region.toString());

        var client = BedrockAgentAsyncClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listAgents(client);
    }

    // snippet-start:[bedrock-agent.java2.list_agents_async.main]
    /**
     * Lists the agents in an account.
     *
     * <p>This method retrieves a list of all agents by making a call
     * to the ListAgents API operation. It prints out basic information
     * about each agent to the console for demonstration purposes.</p>
     *
     * @param client Asynchronous client to manage Agents for Amazon Bedrock
     * @return A list of AgentSummary objects containing details about each agent
     */
    public static List<AgentSummary> listAgents(BedrockAgentAsyncClient client) {
        System.out.println("Retrieving Amazon Bedrock Agent List");

        try {
            ListAgentsRequest request = ListAgentsRequest.builder().build();

            CompletableFuture<ListAgentsResponse> future = client.listAgents(request)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            System.out.println(exception.getMessage());
                        }
                    });

            ListAgentsResponse response = future.get();

            List<AgentSummary> agents = response.agentSummaries();

            for (AgentSummary agent : agents) {
                System.out.println("Name     : " + agent.agentName());
                System.out.println("Agent ID : " + agent.agentId());
                System.out.println("Status   : " + agent.agentStatus());
                System.out.println();
            }

            return agents;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // snippet-end:[bedrock-agent.java2.list_agents_async.main]
}
