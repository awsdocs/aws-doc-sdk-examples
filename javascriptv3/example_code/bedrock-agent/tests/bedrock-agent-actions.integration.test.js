// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { afterAll, beforeAll, describe, expect, it } from 'vitest';

import { getRandomAlphanumericString } from '@aws-sdk-examples/libs/utils/util-string.js';
import { wait } from '@aws-sdk-examples/libs/utils/util-timers.js';
import { log } from '@aws-sdk-examples/libs/utils/util-log.js';

import {
    CreateRoleCommand, DeleteRoleCommand,
    DeleteRolePolicyCommand,
    IAMClient,
    PutRolePolicyCommand,
    waitUntilRoleExists
} from '@aws-sdk/client-iam';

import {createAgent} from '../actions/create-agent.js';
import {listAgentsWithPaginator} from '../actions/list-agents.js';
import {getAgent} from '../actions/get-agent.js';
import {deleteAgent} from '../actions/delete-agent.js';

/**
 * @typedef {Object} Agent
 * @property {string} agentId
 * @property {string} agentName
 * @property {string} agentStatus
 */

const REGION = 'us-east-1';
const AGENT_POLICY_NAME = 'can-invoke-bedrock-model';
const FOUNDATION_MODEL = 'anthropic.claude-v2';

const postfix = getRandomAlphanumericString(8);
const testAgentName = `test-agent-${postfix}`;
const agentRoleName = `AmazonBedrockExecutionRoleForAgents_${postfix}`;
let agentResourceRoleArn;

describe('create-list-get-delete', () => {
    const iam = new IAMClient();

    // Ideally these should be separate tests, but since we're creating
    // real AWS resources, this saves on testing time and cost.
    it('should-successfully-complete', async () => {
        await expect(async () => {
            const agent = await testCreateAgent();
            await waitForAgentStatusNotToBe(agent.agentId, 'CREATING');
            await testListAgents();
            await testGetAgent(agent.agentId);
            await testDeleteAgent(agent.agentId);
        }).does.not.throw();
    });

    beforeAll(async () => {
        try {
            const response = await iam.send(new CreateRoleCommand({
                AssumeRolePolicyDocument: JSON.stringify({
                    Version: '2012-10-17',
                    Statement: [
                        {
                            Effect: 'Allow',
                            Principal: {
                                Service: 'bedrock.amazonaws.com',
                            },
                            Action: 'sts:AssumeRole',
                        },
                    ],
                }),
                RoleName: agentRoleName,
            }));
            agentResourceRoleArn = response.Role ? response.Role.Arn : null;

            await waitUntilRoleExists(
                {
                    client: iam,
                    maxWaitTime: 15,
                },
                {RoleName: agentRoleName},
            );

            const modelArn = `arn:aws:bedrock:${REGION}::foundation-model/${FOUNDATION_MODEL}*`
            await iam.send(new PutRolePolicyCommand({
                RoleName: agentRoleName,
                PolicyName: AGENT_POLICY_NAME,
                PolicyDocument: JSON.stringify({
                    Version: '2012-10-17',
                    Statement: [
                        {
                            Effect: 'Allow',
                            Action: 'bedrock:InvokeModel',
                            Resource: modelArn,
                        },
                    ],
                }),
            }));
        } catch (err) {
            log(err);
            throw err;
        }
    });

    const testCreateAgent = async () => {
        const agent = await createAgent(testAgentName, FOUNDATION_MODEL, agentResourceRoleArn, REGION);
        expect(agent.agentName).toEqual(testAgentName);
        return agent;
    };

    const testListAgents = async () => {
        /** @type {Agent[]} */
        const agents = await listAgentsWithPaginator(REGION);

        expect(agents.length).toBeGreaterThanOrEqual(1);
        expect(agents.map((agent) => agent.agentName)).toContain(testAgentName);
    };

    const testGetAgent = async (agentId) => {
        /** @type {Agent} */
        const agent = await getAgent(agentId, REGION);
        expect(agent.agentName).toEqual(testAgentName);
    };

    const testDeleteAgent = async (agentId) => {
        const result = await deleteAgent(agentId, REGION);
        expect(result.agentStatus).toEqual('DELETING');
    };

    const waitForAgentStatusNotToBe = async (agentId, status) => {
        let exitLoop = false;
        do {
            await wait(2);
            /** @type {Agent} */
            const fetchedAgent = await getAgent(agentId, REGION);
            exitLoop = fetchedAgent.agentStatus !== status;
        } while (!exitLoop)
    };

    afterAll(async () => {
        try {
            await iam.send(new DeleteRolePolicyCommand({
                RoleName: agentRoleName,
                PolicyName: AGENT_POLICY_NAME
            }));
            await iam.send(new DeleteRoleCommand({RoleName: agentRoleName}));
        } catch (err) {
            log(err);
            throw err;
        }
    });
});
