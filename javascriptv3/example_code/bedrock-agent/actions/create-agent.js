// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from 'url';
import { checkForPlaceholders } from "../lib/utils.js";

import { BedrockAgentClient, CreateAgentCommand } from '@aws-sdk/client-bedrock-agent';

/**
 * @typedef {Object} Agent
 */

/**
 * Creates an Amazon Bedrock Agent.
 *
 * @param {string} agentName - A name for the agent that you create.
 * @param {string} foundationModel - The foundation model to be used by the agent you create.
 * @param {string} agentResourceRoleArn - The ARN of the IAM role with permissions required by the agent.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Agent} An object containing details about the created agent.
 */
export const createAgent = async (agentName, foundationModel, agentResourceRoleArn, region = 'us-east-1') => {
    const client = new BedrockAgentClient({region});

    const command = new CreateAgentCommand({
        agentName,
        foundationModel,
        agentResourceRoleArn,
    });
    const response = await client.send(command);

    return response.agent;
};


// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // Replace '[YOUR_AGENT_NAME]' and '[YOUR_AGENT_RESOURCE_ROLE_ARN]' with your own agent's name
    // and the ARN for your agent's resource role.'
    const agentName = '[YOUR_AGENT_NAME]';
    const roleArn = '[YOUR_AGENT_RESOURCE_ROLE_ARN]';
    const foundationModel = 'anthropic-claude-v2';

    try {
        checkForPlaceholders([agentName, roleArn])
    } catch (error) {
        console.error(error.message);
        process.exit(1);
    }

    console.log(`Creating a new agent...`);

    const agent = await createAgent(agentName, foundationModel, roleArn);
    console.log(agent);
}
