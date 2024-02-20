// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect } from 'vitest';

import { listAgentsWithCommandObject, listAgentsWithPaginator } from '../actions/list-agents.js';

describe('list-agents-with-command-object', () => {
    it('should-return-agent-summaries', async () => {
       const agentSummaries = await listAgentsWithCommandObject();
       expect(agentSummaries).not.toBeNull();
    });
});

describe('list-agents-with-paginator', () => {
    it('should-return-agent-summaries', async () => {
        const agentSummaries = await listAgentsWithPaginator();
        expect(agentSummaries).not.toBeNull();
    });
});
