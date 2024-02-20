// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from 'url';

import { BedrockAgentClient, ListAgentsCommand, paginateListAgents } from '@aws-sdk/client-bedrock-agent';

/**
 * @typedef {Object} AgentSummary
 */

/**
 * Retrieves a list of available Amazon Bedrock agents utilizing the ListAgentsCommand.
 *
 * This function demonstrates the manual approach, sending a command to the client and processing the response.
 * Pagination must manually be managed. For a simplified approach that abstracts away pagination logic, see
 * the `listAgentsWithPaginator()` example below.
 *
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Promise<AgentSummary[]>} A promise that resolves to an array of agent summaries.
 */
export const listAgentsWithCommandObject = async (region = 'us-east-1') => {
    const client = new BedrockAgentClient({ region });

    let nextToken;
    const agentSummaries = [];
    do {
        const command = new ListAgentsCommand({
            nextToken,
            maxResults: 10, // optional, added for demonstration purposes
        });

        /** @type {{agentSummaries: AgentSummary[], nextToken?: string}} */
        const paginatedResponse = await client.send(command);

        agentSummaries.push(...(paginatedResponse.agentSummaries || []));

        nextToken = paginatedResponse.nextToken;

    } while (nextToken);

    return agentSummaries;
}

/**
 * Retrieves a list of available Amazon Bedrock agents utilizing the paginator function.
 *
 * This function leverages a paginator, which abstracts the complexity of pagination, providing
 * a straightforward way to handle paginated results inside a `for await...of` loop.
 *
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Promise<AgentSummary[]>} A promise that resolves to an array of agent summaries.
 */
export const listAgentsWithPaginator = async (region = 'us-east-1') => {
    const client = new BedrockAgentClient({ region });

    const paginatorConfig = {
        client,
        pageSize: 10 // optional, added for demonstration purposes
    };

    const paginator = paginateListAgents(paginatorConfig, {});

    // Paginate until there are no more results
    const agentSummaries = [];
    for await (const page of paginator) {
        agentSummaries.push(...page.agentSummaries);
    }

    return agentSummaries;
}


// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    console.log('='.repeat(68));
    console.log('Listing agents using ListAgentsCommand:')
    for (const agent of await listAgentsWithCommandObject()) {
        console.log(agent);
    }

    console.log('='.repeat(68));
    console.log('Listing agents using the paginateListAgents function:')
    for (const agent of await listAgentsWithPaginator()) {
        console.log(agent);
    }
}
