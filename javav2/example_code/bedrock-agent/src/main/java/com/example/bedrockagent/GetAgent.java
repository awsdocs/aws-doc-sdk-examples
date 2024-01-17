// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// snippet-start:[bedrock-agent.java2.get_agent.import]
package com.example.bedrockagent;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentClient;
import software.amazon.awssdk.services.bedrockagent.model.GetAgentRequest;
import software.amazon.awssdk.services.bedrockagent.model.GetAgentResponse;
// snippet-end:[bedrock-agent.java2.get_agent.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class GetAgent {
    public static void main(String[] args) {
        final String usage = """
            
            Usage:
                <agentId>\s
                
            Where:
                agentId - The ID of an existing Amazon Bedrock Agent in your AWS account
        """;

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String agentName = args[0];

        Region region = Region.US_EAST_1;
        BedrockAgentClient client = BedrockAgentClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        getAgent(client, agentName);
    }

    // snippet-start:[bedrock-agent.java2.get_agent.main]
    public static void getAgent(BedrockAgentClient client, String agentId) {
        var request = GetAgentRequest.builder()
                .agentId(agentId)
                .build();
        GetAgentResponse response = client.getAgent(request);
        System.out.println(response);
    }
    // snippet-start:[bedrock-agent.java2.get_agent.main]
}
