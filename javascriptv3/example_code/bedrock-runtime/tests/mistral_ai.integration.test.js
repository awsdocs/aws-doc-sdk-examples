// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { FoundationModels } from "../config/foundation_models.js";
import { expectToBeANonEmptyString } from "./test_tools.js";
import { invokeModel as invokeMistral } from "../models/mistral/invoke_mistral_7b.js";
import { invokeModel as invokeMixtral } from "../models/mistral/invoke_mixtral_8x7b.js";

const TEXT_PROMPT = "Hello, this is a test prompt";

describe("Invoke Mistral 7B", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.MISTRAL_7B.modelId;
    const response = await invokeMistral(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Mixtral 8x7B", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.MIXTRAL_8X7B.modelId;
    const response = await invokeMixtral(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});
