// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect } from "vitest";

import { invokeJurassic2 } from "../actions/invoke-jurassic2.js";
import { invokeLlama2 } from "../actions/invoke-llama2.js";
import { invokeTitanTextExpressV1 } from "../actions/invoke-titan-text-express-v1.js";

const TEST_PROMPT = "Hello, this is a test prompt";

describe("invoke jurassic-2 with test prompt", () => {
  it("should return a text completion", async () => {
    const response = await invokeJurassic2(TEST_PROMPT);
    expect(typeof response).toBe("string");
    expect(response).not.toBe("");
  });
});

describe("invoke llama-2 with test prompt", () => {
  it("should return a text completion", async () => {
    const response = await invokeLlama2(TEST_PROMPT);
    expect(typeof response).toBe("string");
    expect(response).not.toBe("");
  });
});

describe("invoke titan-text-express-v1 with test prompt", () => {
  it("should return a text completion", async () => {
    const response = await invokeTitanTextExpressV1(TEST_PROMPT);
    expect(typeof response).toBe("object");
    for (const result of response) {
      expect(result).toHaveProperty("outputText");
      expect(result.outputText).not.toBe("");
    }
  });
});
