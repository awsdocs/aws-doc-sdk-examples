// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockagent.sync;

// snippet-start:[bedrock-agent.java2.list_agent_action_groups.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.ActionGroupSummary;
import software.amazon.awssdk.services.bedrockagent.model.BedrockAgentException;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentActionGroupsRequest;
import software.amazon.awssdk.services.bedrockagent.model.ListAgentActionGroupsResponse;

import java.util.List;
// snippet-end:[bedrock-agent.java2.list_agent_action_groups.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class ListAgentActionGroups {
    public static void main(String[] args) {
        final String usage = """
            
            Usage:
                <agentId> <agentVersion> [<region>]\s
                
            Where:
                agentId - The ID of an existing Agent in your AWS account
                agentVersion - The version of the agent
                region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'
        """;

        if (args.length < 2 || args.length > 3) {
            System.out.println(usage);
            System.exit(1);
        }

        String agentId = args[0];
        String agentVersion = args[1];

        Region region = args.length == 3 ? Region.of(args[2]) : Region.US_EAST_1;

        System.out.println("Initializing the Amazon Bedrock Agent Client...");
        System.out.printf("Region: %s%n", region.toString());

        var client = BedrockAgentClient.builder().region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        listAgentActionGroups(client, agentId, agentVersion);
    }

    // snippet-start:[bedrock-agent.java2.list_agent_action_groups.main]
    /**
     * Lists the action groups for a specific version of an agent.
     *
     * <p>This method retrieves a list of all action groups of a version of an agent
     * by making a call to the ListAgentActionGroups API operation. It prints out basic
     * information about each action group to the console for demonstration purposes.</p>
     *
     * @param client       The client to manage Agents for Amazon Bedrock
     * @param agentId      The unique identifier of the agent
     * @param agentVersion The version of the agent
     * @return A list of ActionGroupSummary objects containing details about each action group
     */
    public static List<ActionGroupSummary> listAgentActionGroups(BedrockAgentClient client, String agentId, String agentVersion) {
        System.out.printf("Retrieving Action Groups for Agent ID %s, Version %s%n", agentId, agentVersion);

        try {
            ListAgentActionGroupsRequest request = ListAgentActionGroupsRequest.builder()
                    .agentId(agentId)
                    .agentVersion(agentVersion)
                    .build();

            ListAgentActionGroupsResponse response = client.listAgentActionGroups(request);

            List<ActionGroupSummary> actionGroups = response.actionGroupSummaries();

            if (actionGroups.isEmpty()) {
                System.out.printf("No action groups in agent: %s, version: %s.%n", agentId, agentVersion);
            } else {
                for (ActionGroupSummary actionGroup : actionGroups) {
                    System.out.println(" Action Group ID    : " + actionGroup.actionGroupId());
                    System.out.println(" Action Group Name  : " + actionGroup.actionGroupName());
                    System.out.println(" Action Group State : " + actionGroup.actionGroupState());
                    System.out.println();
                }
            }

            return actionGroups;

        } catch (BedrockAgentException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            throw e;
        }
    }
    // snippet-end:[bedrock-agent.java2.list_agent_action_groups.main]
}
