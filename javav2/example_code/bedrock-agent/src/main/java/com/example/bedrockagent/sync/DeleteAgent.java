// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockagent.sync;

// snippet-start:[bedrock-agent.java2.delete_agent.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.AgentStatus;
import software.amazon.awssdk.services.bedrockagent.model.BedrockAgentException;
import software.amazon.awssdk.services.bedrockagent.model.DeleteAgentRequest;
import software.amazon.awssdk.services.bedrockagent.model.DeleteAgentResponse;
// snippet-end:[bedrock-agent.java2.delete_agent.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class DeleteAgent {
    public static void main(String[] args) {
        final String usage = """
            
            Usage:
                <agentId> [<region>]\s
                
            Where:
                agentId - The ID of an existing Agent in your AWS account
                region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'
        """;

        if (args.length < 1 || args.length > 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String agentId = args[0];

        Region region = args.length == 2 ? Region.of(args[1]) : Region.US_EAST_1;

        System.out.println("Initializing the Amazon Bedrock Agent Client...");
        System.out.printf("Region: %s%n", region.toString());

        var client = BedrockAgentClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        deleteAgent(client, agentId);
    }

    // snippet-start:[bedrock-agent.java2.delete_agent.main]
    /**
     * Deletes an agent.
     *
     * @param client  The client to manage Agents for Amazon Bedrock
     * @param agentId The unique identifier of the agent
     * @return An AgentStatus object
     */
    public static AgentStatus deleteAgent(BedrockAgentClient client, String agentId) {
        System.out.printf("Deleting the Amazon Bedrock Agent with ID: %s...%n", agentId);

        try {
            DeleteAgentRequest request = DeleteAgentRequest.builder()
                    .agentId(agentId)
                    .build();

            DeleteAgentResponse response = client.deleteAgent(request);

            return response.agentStatus();
        } catch (BedrockAgentException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    // snippet-end:[bedrock-agent.java2.delete_agent.main]
}
