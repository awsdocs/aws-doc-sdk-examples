// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { fileURLToPath } from 'url';
import { checkForPlaceholders } from "../lib/utils.js";

import { BedrockAgentClient, PrepareAgentCommand } from '@aws-sdk/client-bedrock-agent';
import { wait } from "@aws-sdk-examples/libs/utils/util-timers.js";
import { getAgent } from "./get-agent.js";

/**
 * @typedef {Object} Agent
 * @property {string} agentStatus
 */

/**
 * Creates a DRAFT version of the agent that can be used for internal testing.
 *
 * @param {string} agentId - The unique identifier of the agent.
 * @param {string} [region='us-east-1'] - The AWS region in use.
 * @returns {Object} An object containing the agent id, version, and status along with some metadata.
 */
export const prepareAgent = async (agentId, region = 'us-east-1') => {
    const client = new BedrockAgentClient({region});

    const command = new PrepareAgentCommand({agentId});
    return await client.send(command);
}

const waitForAgentStatusNotToBe = async (agentId, status, region = 'us-east-1') => {
    let exitLoop = false;
    let agent;
    do {
        await wait(2);
        /** @type {Agent} */
        agent = await getAgent(agentId, region);
        exitLoop = agent.agentStatus !== status;
    } while (!exitLoop)

    return agent;
};

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
    console.log(`Preparing agent with ID ${agentId}...`);
    const response = await prepareAgent(agentId);
    console.log(` Agent version: ${response.agentVersion}`)

    console.log(` Agent status: ${response.agentStatus}`)

    console.log(`Waiting for preparation to finish...`);
    /** @type {Agent} */
    const agent = await waitForAgentStatusNotToBe(response.agentId, 'PREPARING');
    const agentStatus = agent.agentStatus;
    console.log(` Agent status: ${agentStatus}`)
    if (agentStatus === 'FAILED') {
        console.log(`Note: An agent status of 'FAILED' doesn't indicate an error. Preparation always fails if the agent doesn't have an action group attached.`)
    }
}
