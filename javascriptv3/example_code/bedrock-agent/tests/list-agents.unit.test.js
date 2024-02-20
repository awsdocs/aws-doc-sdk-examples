// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {describe, expect, it, vi} from 'vitest';

import { BedrockAgentClient, ListAgentsCommand } from '@aws-sdk/client-bedrock-agent';

import { listAgentsWithCommandObject, listAgentsWithPaginator } from '../actions/list-agents.js';

const testAgent1 = { agentId: 'TEST_AGENT_1_ID' };
const testAgent2 = { agentId: 'TEST_AGENT_2_ID' };

vi.mock('@aws-sdk/client-bedrock-agent', () => {
    return {
        ListAgentsCommand: class {},
        BedrockAgentClient: class {
            send() {
                return Promise.resolve({
                    agentSummaries: [ testAgent1, testAgent2 ]
                });
            }
        },
        paginateListAgents: vi.fn(() => async function* () {
            yield* [
                { agentSummaries: [ testAgent1 ] },
                { agentSummaries: [ testAgent2 ] },
            ];
        }()),
    };
});

describe('list-agents-with-command-object', () => {
    it('should-return-mocked-agent-summaries', async () => {
        const spy = vi.spyOn(BedrockAgentClient.prototype, 'send');
        const agents = await listAgentsWithCommandObject();

        expect(spy).toHaveBeenCalledWith(new ListAgentsCommand());
        expect(agents).toHaveLength(2);
        expect(agents).toContainEqual(testAgent1);
        expect(agents).toContainEqual(testAgent2);
    });
});

describe('list-agents-with-paginator', () => {
    it('should-return-mocked-agent-summaries', async () => {
        const agents = await listAgentsWithPaginator();

        expect(agents).toHaveLength(2);
        expect(agents).toContainEqual(testAgent1);
        expect(agents).toContainEqual(testAgent2);
    });
});
