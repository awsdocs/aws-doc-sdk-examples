// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.bedrockagent.async;

// snippet-start:[bedrock-agent.java2.create_agent.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockagent.BedrockAgentAsyncClient;
import software.amazon.awssdk.services.bedrockagent.model.Agent;
import software.amazon.awssdk.services.bedrockagent.model.CreateAgentRequest;
import software.amazon.awssdk.services.bedrockagent.model.CreateAgentResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
// snippet-end:[bedrock-agent.java2.create_agent.import]

/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateAgentAsync {
    public static void main(String[] args) {
        final String usage = """
            
            Usage:
                <agentName> <roleArn> <foundationModel> [<region>]\s
                
            Where:
                agentName - A name for the agent
                roleArn - The ARN of the IAM role with permissions needed by the agent
                foundationModel - The foundation model to be used by the agent, e.g. 'anthropic.claude-v2'
                region - (Optional) The AWS region where the Agent is located. Default is 'us-east-1'
        """;

        if (args.length < 3 || args.length > 4) {
            System.out.println(usage);
            System.exit(1);
        }

        String agentName = args[0];
        String roleArn = args[1];
        String foundationModel = args[2];

        Region region = args.length == 4 ? Region.of(args[3]) : Region.US_EAST_1;

        System.out.println("Initializing the Amazon Bedrock Agent Client...");
        System.out.printf("Region: %s%n", region.toString());

        var client = BedrockAgentAsyncClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        createAgent(client, agentName, roleArn, foundationModel);
    }

    // snippet-start:[bedrock-agent.java2.create_agent.main]
    /**
     * Creates an agent that can orchestrate interactions between foundation models,
     * data sources, software applications, user conversations, and APIs to carry
     * out tasks to help customers.
     *
     * @param client          The client to manage Agents for Amazon Bedrock
     * @param agentName       A name for the agent
     * @param roleArn         The ARN of the IAM role with permissions needed by the agent
     * @param foundationModel The foundation model to be used by the agent
     * @return The created Agent object
     */
    public static Agent createAgent(BedrockAgentAsyncClient client, String agentName, String roleArn, String foundationModel) {
        System.out.printf("Creating an Amazon Bedrock Agent based on %s with name: %s...%n", foundationModel, agentName);
        try {
            CreateAgentRequest request = CreateAgentRequest.builder()
                    .agentName(agentName)
                    .agentResourceRoleArn(roleArn)
                    .foundationModel(foundationModel)
                    .build();

            CompletableFuture<CreateAgentResponse> future = client.createAgent(request)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            System.out.println(exception.getMessage());
                        }
                    });

            CreateAgentResponse response = future.get();

            Agent agent = response.agent();

            System.out.println(" Agent ID : " + agent.agentId());
            System.out.println(" Name     : " + agent.agentName());
            System.out.println(" Model    : " + agent.foundationModel());
            System.out.println(" Status   : " + agent.agentStatus());

            return agent;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
    // snippet-end:[bedrock-agent.java2.create_agent.main]
}
