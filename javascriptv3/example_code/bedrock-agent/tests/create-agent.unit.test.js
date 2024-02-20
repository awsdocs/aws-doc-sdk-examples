// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, it, vi } from 'vitest';

import { BedrockAgentClient, CreateAgentCommand } from '@aws-sdk/client-bedrock-agent';
import { createAgent } from '../actions/create-agent.js';

const agentName = 'TEST_AGENT_NAME';
const foundationModel = 'TEST_MODEL';
const testRoleArn = 'TEST_ROLE_ARN';

const testAgent = {
    agentId: 'NEW_AGENT_ID',
    agentName: agentName,
    foundationModel: foundationModel,
    agentResourceRoleArn: testRoleArn,
};

vi.mock('@aws-sdk/client-bedrock-agent', () => {
    return {
        CreateAgentCommand: class {
            constructor(params) {
                CreateAgentCommand.params = params;
            }
        },
        BedrockAgentClient: class {
            send() {
                return Promise.resolve({ agent: testAgent });
            }
        },
    };
});

describe('create-agent', () => {
    it('should-return-mocked-agent', async () => {
        const spy = vi.spyOn(BedrockAgentClient.prototype, 'send');
        const params = {
            agentName,
            foundationModel,
            agentResourceRoleArn: testRoleArn
        };

        const agent = await createAgent(params.agentName, params.foundationModel, params.agentResourceRoleArn);

        expect(vi.mocked(CreateAgentCommand).params).toEqual(params);
        expect(spy).toHaveBeenCalledTimes(1);
        expect(spy).toHaveBeenCalledWith(new CreateAgentCommand(params));
        expect(agent).toEqual(testAgent);
    });
});
