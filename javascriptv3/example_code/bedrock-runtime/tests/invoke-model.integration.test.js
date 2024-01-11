import { describe, it, expect } from "vitest";
import { invokeClaude } from '../action/invoke-model.js';

describe('invoke claude v2', () => {
    it('should return a completion', async () => {
        const response = await invokeClaude();
        expect(response).not.toBe(null);
        expect(typeof response).toBe('string');
    })
})