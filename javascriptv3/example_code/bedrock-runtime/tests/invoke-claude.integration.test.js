// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {describe, it} from "vitest";
import {FoundationModels} from "../foundation-models.js";
import {
    invokeMessagesApi as invokeClaudeInstantMessagesApi,
    invokeTextCompletionsApi as invokeClaudeInstantTextCompletionsApi,
} from "../anthropic/claude-instant.js";
import {
    invokeTextCompletionsApi as invokeClaude2TextCompletionsApi,
} from "../anthropic/claude-2.js";
import {expectToBeANonEmptyString} from "./test-tools.js";

const TEXT_PROMPT = "Hello, this is a test prompt";



describe("Invoke Anthropic Claude Instant using the Text Completions API", () => {
    it("should return a response", async () => {
        const modelId = FoundationModels.CLAUDE_INSTANT.modelId;
        const response = await invokeClaudeInstantTextCompletionsApi(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response);
    })
});

describe("Invoke Anthropic Claude Instant using the Messages API", () => {
    it("should return a response", async () => {
        const modelId = FoundationModels.CLAUDE_INSTANT.modelId;
        const response = await invokeClaudeInstantMessagesApi(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response);
    })
});

describe("Invoke Anthropic Claude 2.0 using the Text Completions API", () => {
    it("should return a text completion", async () => {
        const modelId = FoundationModels.CLAUDE_2.modelId;
        const response = await invokeClaude2TextCompletionsApi(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response)
    });
});

describe("Invoke Anthropic Claude 2.1 using the Text Completions API", () => {
    it("should return a text completion", async () => {
        const modelId = FoundationModels.CLAUDE_2_1.modelId;
        const response = await invokeClaude2TextCompletionsApi(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response)
    });
});
