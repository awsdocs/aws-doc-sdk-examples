// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, it, vi } from 'vitest';

import { BedrockAgentClient, ListAgentActionGroupsCommand } from '@aws-sdk/client-bedrock-agent';

import {
    listAgentActionGroupsWithCommandObject,
    listAgentActionGroupsWithPaginator
} from "../actions/list-agent-action-groups.js";

vi.mock('@aws-sdk/client-bedrock-agent', () => {
    const actionGroup = { actionGroupId: "1" }
    return {
        ListAgentActionGroupsCommand: class {},
        BedrockAgentClient: class {
            send() {
                return Promise.resolve({ actionGroupSummaries: [ actionGroup ] });
            }
        },
        paginateListAgentActionGroups: vi.fn(() => async function* () {
            yield { actionGroupSummaries: [actionGroup] };
        }()),
    };
});

describe('list-agent-action-groups-with-command-object', () => {
    it('should-return-mocked-agent-summaries', async () => {
        const spy = vi.spyOn(BedrockAgentClient.prototype, 'send');

        const agents = await listAgentActionGroupsWithCommandObject();

        expect(spy).toHaveBeenCalledWith(new ListAgentActionGroupsCommand());
        expect(agents).toHaveLength(1);
        expect(agents).toContainEqual({ actionGroupId: "1" });
    });
});

describe('list-agent-action-groups-with-paginator', () => {
    it('should-return-mocked-agent-summaries', async () => {

        const agents = await listAgentActionGroupsWithPaginator();

        expect(agents).toHaveLength(1);
        expect(agents).toContainEqual({actionGroupId: "1"});
    })
});
