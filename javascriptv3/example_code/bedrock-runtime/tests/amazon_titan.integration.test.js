// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { FoundationModels } from "../config/foundation_models.js";
import { expectToBeANonEmptyString } from "./test_tools.js";
import { invokeModel } from "../models/amazonTitanText/invoke_model.js";

const TEXT_PROMPT = "Hello, this is a test prompt";

describe("Invoke Titan Text G1 - Express", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.TITAN_TEXT_G1_EXPRESS.modelId;
    const response = await invokeModel(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});

describe("Invoke Titan Text G1 - Lite", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.TITAN_TEXT_G1_LITE.modelId;
    const response = await invokeModel(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response);
  });
});
