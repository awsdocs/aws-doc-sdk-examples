// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, it, vi } from 'vitest';

import { BedrockAgentClient, DeleteAgentCommand } from '@aws-sdk/client-bedrock-agent';
import { deleteAgent } from '../actions/delete-agent.js';

const agentId = 'TEST_AGENT_ID';

vi.mock('@aws-sdk/client-bedrock-agent', () => {
    return {
        DeleteAgentCommand: class {
            constructor(params) {
                DeleteAgentCommand.params = params;
            }
        },
        BedrockAgentClient: class {
            send() {
                return Promise.resolve({ agentId });
            }
        },
    };
});

describe('delete-agent', () => {
    it('should-delete-mocked-agent', async () => {
        const spy = vi.spyOn(BedrockAgentClient.prototype, 'send');
        const params = { agentId };

        const response = await deleteAgent(agentId);

        expect(vi.mocked(DeleteAgentCommand).params).toEqual(params);
        expect(spy).toHaveBeenCalledTimes(1);
        expect(spy).toHaveBeenCalledWith(new DeleteAgentCommand(params));
        expect(response.agentId).toEqual(agentId);
    });
});
