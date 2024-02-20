// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from 'url';
import { checkForPlaceholders } from "../lib/utils.js";

import { BedrockAgentClient, GetAgentCommand } from '@aws-sdk/client-bedrock-agent';

/**
 * Retrieves the details of an Amazon Bedrock Agent.
 *
 * @param {string} agentId - The unique identifier of the agent.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Agent} An object containing the agent details.
 */
export const getAgent = async (agentId, region = 'us-east-1') => {
    const client = new BedrockAgentClient({region});

    const command = new GetAgentCommand({agentId});
    const response = await client.send(command);
    return response.agent;
}


// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // Replace '[YOUR_AGENT_ID]' with your own agent's id.
    const agentId = '[YOUR_AGENT_ID]';

    try {
        checkForPlaceholders([agentId])
    } catch (error) {
        console.error(error.message);
        process.exit(1);
    }

    console.log(`Retrieving agent with ID ${agentId}...`);

    const agent = await getAgent(agentId);
    console.log(agent);
}
