// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from 'url';

import { BedrockAgentClient, GetAgentCommand, paginateListAgents } from '@aws-sdk/client-bedrock-agent';

/**
 * @typedef {Object} AgentSummary
 */

/**
 * A simple scenario to demonstrate basic setup and interaction with the Bedrock Agents Client.
 *
 * This function first initializes the Amazon Bedrock Agents client for a specific region.
 * It then retrieves a list of existing agents using the streamlined paginator approach.
 * For each agent found, it retrieves detailed information using a command object.
 *
 * Demonstrates:
 * - Use of the Bedrock Agents client to initialize and communicate with the AWS service.
 * - Listing resources in a paginated response pattern.
 * - Accessing an individual resource using a command object.
 *
 * @async
 * @returns {Promise<void>} A promise that resolves when the function has completed execution.
 */
export const main = async () => {
    const region = 'us-east-1';

    console.log('='.repeat(68));

    console.log(`Initializing Amazon Bedrock Agents client for ${region}...`);
    const client = new BedrockAgentClient({ region });

    console.log(`Retrieving the list of existing agents...`);
    const paginatorConfig = { client };
    const paginator = paginateListAgents(paginatorConfig, {});

    /** @type {AgentSummary[]} */
    const agentSummaries = [];
    for await (const page of paginator) {
        agentSummaries.push(...page.agentSummaries);
    }

    console.log(`Found ${agentSummaries.length} agents in ${region}.`);

    if (agentSummaries.length > 0) {
        for (const agentSummary of agentSummaries) {
            const agentId = agentSummary.agentId;
            console.log('='.repeat(68));
            console.log(`Retrieving agent with ID: ${agentId}:`);
            console.log('-'.repeat(68));

            const command = new GetAgentCommand({ agentId });
            const response = await client.send(command);
            const agent = response.agent;

            console.log(` Name: ${agent.agentName}`);
            console.log(` Status: ${agent.agentStatus}`);
            console.log(` ARN: ${agent.agentArn}`);
            console.log(` Foundation model: ${agent.foundationModel}`);
        }
    }
    console.log('='.repeat(68));
};


// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    await main();
}
