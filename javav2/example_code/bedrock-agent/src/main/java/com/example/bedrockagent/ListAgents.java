// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[bedrock-agent.java2.list_agents.import]
package com.example.bedrockagent;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.AgentSummary;
import software.amazon.awssdk.services.bedrockagent.model.BedrockAgentException;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentsRequest;

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
        Region region = Region.US_EAST_1;
        BedrockAgentClient client = BedrockAgentClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listAgents(client);
    }

    // snippet-start:[bedrock-agent.java2.list_agents.main]
    /**
     * Lists the agents belonging to an account and information about each agent.
     *
     * <p>This method retrieves a list of all agents by making a call
     * to the ListAgents API operation. It prints out basic information
     * about each agent to the console for demonstration purposes.</p>
     *
     * @param client Client for accessing Agents for Amazon Bedrock
     * @return A list of AgentSummary objects containing details about each agent
     * @throws BedrockAgentException If the API call fails
     */
    public static List<AgentSummary> listAgents(BedrockAgentClient client) {
        try {
            var request = ListAgentsRequest.builder().build();
            var response = client.listAgents(request);
            List<AgentSummary> agents = response.agentSummaries();

            for (AgentSummary agent : agents) {
                System.out.println("Name       : " + agent.agentName());
                System.out.println("Agent ID   : " + agent.agentId());
                System.out.println("Status     : " + agent.agentStatus());
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
