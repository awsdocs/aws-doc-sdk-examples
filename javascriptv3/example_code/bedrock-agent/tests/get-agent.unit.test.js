// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, it, vi } from 'vitest';

import { BedrockAgentClient, GetAgentCommand } from '@aws-sdk/client-bedrock-agent';
import { getAgent } from "../actions/get-agent.js";

const testAgentId = 'TEST_AGENT_ID';

const testAgent = {
    agentId: testAgentId,
    agentVersion: 'TEST_AGENT_VERSION',
};

vi.mock('@aws-sdk/client-bedrock-agent', () => {
    return {
        GetAgentCommand: class {
            constructor(params) {
                GetAgentCommand.params = params;
            }
        },
        BedrockAgentClient: class {
            send() {
                return Promise.resolve({ agent: testAgent });
            }
        },
    }
});

describe('get-agent', () => {
    it('should-return-mocked-agent', async () => {
        const spy = vi.spyOn(BedrockAgentClient.prototype, 'send');
        const params = { agentId: testAgentId };

        const agent = await getAgent(testAgentId);

        expect(vi.mocked(GetAgentCommand).params).toEqual(params)
        expect(spy).toHaveBeenCalledTimes(1);
        expect(spy).toHaveBeenCalledWith(new GetAgentCommand(params));
        expect(agent).toEqual(testAgent);
    });
});
