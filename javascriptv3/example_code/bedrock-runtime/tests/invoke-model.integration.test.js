// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect } from "vitest";

import { invokeClaude } from '../actions/invoke-claude.js';
import { invokeJurassic2 } from '../actions/invoke-jurassic2.js';
import { invokeLlama2 } from '../actions/invoke-llama2.js';

const TEST_PROMPT = 'Hello, this is a test prompt';

describe('invoke claude with test prompt', () => {
    it('should return a text completion', async () => {
        const response = await invokeClaude(TEST_PROMPT);
        expect(typeof response).toBe('string');
        expect(response).not.toBe('');
    })
})

describe('invoke jurassic-2 with test prompt', () => {
    it('should return a text completion', async () => {
        const response = await invokeJurassic2(TEST_PROMPT);
        expect(typeof response).toBe('string');
        expect(response).not.toBe('');
    })
})

describe('invoke llama-2 with test prompt', () => {
    it('should return a text completion', async () => {
        const response = await invokeLlama2(TEST_PROMPT);
        expect(typeof response).toBe('string');
        expect(response).not.toBe('');
    })
})
