// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {fileURLToPath} from 'url';

import {BedrockAgentClient, DeleteAgentCommand} from '@aws-sdk/client-bedrock-agent';

/**
 * @typedef {Object} DeleteAgentCommandOutput
 * @property {string|undefined} agentId
 * @property {Object|undefined} agentStatus
 */

/**
 * Deletes an Amazon Bedrock Agent.
 *
 * @param {string} agentId - The unique identifier of the agent to delete.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {DeleteAgentCommandOutput} An object containing the agent id and status.
 */
export const deleteAgent = async (agentId, region = 'us-east-1') => {
    const client = new BedrockAgentClient({region});
    return await client.send(new DeleteAgentCommand({ agentId }));
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    const agentId = '[YOUR_AGENT_ID]';
    console.log(`Deleting agent with ID ${agentId}...`);

    const response = await deleteAgent(agentId);
    console.log(response);
}
