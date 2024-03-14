// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {describe, it} from "vitest";
import {FoundationModels} from "../foundation-models.js";
import {invokeMessagesApi, invokeTextCompletionsApi} from "../anthropic/claude_instant.js";
import {expectToBeANonEmptyString} from "./test-tools.js";

const TEXT_PROMPT = "Hello, this is a test prompt";



describe("Invoke Anthropic Claude Instant using the Text Completions API", () => {
    it("should return a text completion", async () => {
        const modelId = FoundationModels.CLAUDE_INSTANT.modelId;
        const response = await invokeTextCompletionsApi(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response);
    })
});

describe("Invoke Anthropic Claude Instant using the Messages API", () => {
    it("should return a text completion", async () => {
        const modelId = FoundationModels.CLAUDE_INSTANT.modelId;
        const response = await invokeMessagesApi(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response);
    })
});
