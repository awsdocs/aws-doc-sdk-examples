// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect } from "vitest";

import { invokeBedrockAgent } from "../actions/invoke-agent.js";

const TEST_PROMPT = `Hello, what's your name?`;
const SESSION_ID = "aaa"; //generate a random session Id. Pattern: ^[0-9a-zA-Z]+$

describe("invoke agent with test prompt", () => {
  it("should return a text completion for the prompt", async () => {
    const response = await invokeBedrockAgent(TEST_PROMPT, SESSION_ID);
    expect(response.sessionId).not.toBe("");
    expect(response.completion).not.toBe("");
    expect(response.completion).toContain("My name is");
  });
});
