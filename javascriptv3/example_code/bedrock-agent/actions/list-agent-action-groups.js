// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from 'url';
import { checkForPlaceholders } from "../lib/utils.js";

import {
    BedrockAgentClient,
    ListAgentActionGroupsCommand,
    paginateListAgentActionGroups
} from '@aws-sdk/client-bedrock-agent';

/**
 * @typedef {Object} ActionGroupSummary
 */

/**
 * Retrieves a list of Action Groups of an agent utilizing the ListAgentActionGroupsCommand.
 *
 * This function demonstrates the manual approach, sending a command to the client and processing the response.
 * Pagination must manually be managed. For a simplified approach that abstracts away pagination logic, see
 * the `listAgentActionGroupsWithPaginator()` example below.
 *
 * @param {string} agentId - The unique identifier of the agent.
 * @param {string} agentVersion - The version of the agent.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Promise<ActionGroupSummary[]>} A promise that resolves to an array of action group summaries.
 */
export const listAgentActionGroupsWithCommandObject = async (agentId, agentVersion, region = 'us-east-1') => {
    const client = new BedrockAgentClient({ region });

    let nextToken;
    const actionGroupSummaries = [];
    do {
        const command = new ListAgentActionGroupsCommand({
            agentId,
            agentVersion,
            nextToken,
            maxResults: 10, // optional, added for demonstration purposes
        });

        /** @type {{actionGroupSummaries: ActionGroupSummary[], nextToken?: string}} */
        const response = await client.send(command);

        for (const actionGroup of response.actionGroupSummaries || []) {
            actionGroupSummaries.push(actionGroup);
        }

        nextToken = response.nextToken;

    } while (nextToken);

    return actionGroupSummaries;
};

/**
 * Retrieves a list of Action Groups of an agent utilizing the paginator function.
 *
 * This function leverages a paginator, which abstracts the complexity of pagination, providing
 * a straightforward way to handle paginated results inside a `for await...of` loop.
 *
 * @param {string} agentId - The unique identifier of the agent.
 * @param {string} agentVersion - The version of the agent.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Promise<ActionGroupSummary[]>} A promise that resolves to an array of action group summaries.
 */
export const listAgentActionGroupsWithPaginator = async (agentId, agentVersion, region = 'us-east-1') => {
    const client = new BedrockAgentClient({ region });

    // Create a paginator configuration
    const paginatorConfig = {
        client,
        pageSize: 10 // optional, added for demonstration purposes
    };

    const params = { agentId, agentVersion };

    const paginator = paginateListAgentActionGroups(paginatorConfig, params);

    // Paginate until there are no more results
    const actionGroupSummaries  = []
    for await (const page of paginator) {
        actionGroupSummaries.push(...page.actionGroupSummaries);
    }

    return actionGroupSummaries;
};


// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
    // Replace '[YOUR_AGENT_ID]' and [YOUR_AGENT_VERSION] with your own agent's id and version, e.g. 'DRAFT'.
    const agentId = '[YOUR_AGENT_ID]';
    const agentVersion = '[YOUR_AGENT_VERSION]';

    try {
        checkForPlaceholders([agentId, agentVersion])
    } catch (error) {
        console.error(error.message);
        process.exit(1);
    }

    console.log('='.repeat(68));
    console.log('Listing agent action groups using ListAgentActionGroupsCommand:')

    for (const actionGroup of await listAgentActionGroupsWithCommandObject(agentId, agentVersion)) {
        console.log(actionGroup);
    }

    console.log('='.repeat(68));
    console.log('Listing agent action groups using the paginateListAgents function:')
    for (const actionGroup of await listAgentActionGroupsWithPaginator(agentId, agentVersion)) {
        console.log(actionGroup);
    }
}
