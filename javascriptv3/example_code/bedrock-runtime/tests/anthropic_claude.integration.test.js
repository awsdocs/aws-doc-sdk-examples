// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { FoundationModels } from "../config/foundation_models.js";
import { expectToBeANonEmptyString } from "./test_tools.js";
import {
  invokeModel as invokeClaudeInstantMessagesApi,
  invokeTextCompletionsApi as invokeClaudeInstantTextCompletionsApi,
} from "../models/anthropicClaude/invoke_claude_instant_1.js";
import {
  invokeModel as invokeClaude2MessagesApi,
  invokeTextCompletionsApi as invokeClaude2TextCompletionsApi,
} from "../models/anthropicClaude/invoke_claude_2.js";
import {
  invokeModel as invokeClaude3,
  invokeModelWithResponseStream as invokeClaude3WithResponseStream,
} from "../models/anthropicClaude/invoke_claude_3.js";

const TEXT_PROMPT = "Hello, this is a test prompt";

describe("Invoke Anthropic Claude Instant using the Text Completions API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_INSTANT.modelId;
    const response = await invokeClaudeInstantTextCompletionsApi(
      TEXT_PROMPT,
      modelId,
    );
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude Instant using the Messages API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_INSTANT.modelId;
    const response = await invokeClaudeInstantMessagesApi(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 2.0 using the Text Completions API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_2.modelId;
    const response = await invokeClaude2TextCompletionsApi(
      TEXT_PROMPT,
      modelId,
    );
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 2.0 using the Messages API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_2.modelId;
    const response = await invokeClaude2MessagesApi(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 2.1 using the Text Completions API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_2_1.modelId;
    const response = await invokeClaude2TextCompletionsApi(
      TEXT_PROMPT,
      modelId,
    );
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 2.1 using the Messages API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_2_1.modelId;
    const response = await invokeClaude2MessagesApi(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 3 Haiku using the Messages API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_3_HAIKU.modelId;
    const response = await invokeClaude3(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 3 Sonnet using the Messages API", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_3_SONNET.modelId;
    const response = await invokeClaude3(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Anthropic Claude 3 Haiku with a response stream", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.CLAUDE_3_HAIKU.modelId;
    const response = await invokeClaude3WithResponseStream(
      TEXT_PROMPT,
      modelId,
    );
    expectToBeANonEmptyString(response);
  });
});
