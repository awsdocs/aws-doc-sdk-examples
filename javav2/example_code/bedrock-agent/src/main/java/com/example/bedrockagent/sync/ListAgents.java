// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockagent.sync;

// snippet-start:[bedrock-agent.java2.list_agents.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.AgentSummary;
import software.amazon.awssdk.services.bedrockagent.model.BedrockAgentException;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentsRequest;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentsResponse;

import java.util.List;
// snippet-end:[bedrock-agent.java2.list_agents.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListAgents {
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

        var client = BedrockAgentClient.builder().region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listAgents(client);
    }

    // snippet-start:[bedrock-agent.java2.list_agents.main]
    /**
     * Lists the agents in an account.
     *
     * <p>This method retrieves a list of all agents by making a call
     * to the ListAgents API operation. It prints out basic information
     * about each agent to the console for demonstration purposes.</p>
     *
     * @param client The client to manage Agents for Amazon Bedrock
     * @return A list of AgentSummary objects containing details about each agent
     */
    public static List<AgentSummary> listAgents(BedrockAgentClient client) {
        System.out.println("Retrieving Amazon Bedrock Agent List");

        try {
            ListAgentsRequest request = ListAgentsRequest.builder().build();
            ListAgentsResponse response = client.listAgents(request);

            List<AgentSummary> agents = response.agentSummaries();

            for (AgentSummary agent : agents) {
                System.out.println("Name     : " + agent.agentName());
                System.out.println("Agent ID : " + agent.agentId());
                System.out.println("Status   : " + agent.agentStatus());
                System.out.println();
            }

            return agents;
        } catch (BedrockAgentException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    // snippet-end:[bedrock-agent.java2.list_agents.main]
}
