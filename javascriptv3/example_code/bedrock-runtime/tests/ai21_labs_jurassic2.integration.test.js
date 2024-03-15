// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { FoundationModels } from "../tools/foundation_models.js";
import { expectToBeANonEmptyString } from "./test_tools.js";
import { invokeModel } from "../models/ai21_labs_jurassic2/jurassic2.js";

const TEXT_PROMPT = "Hello, this is a test prompt";

describe("Invoke Jurassic2 Mid", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.JURASSIC2_MID.modelId;
    const response = await invokeModel(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response[0]);
  });
});

describe("Invoke Jurassic2 Ultra", () => {
  it("should return a response", async () => {
    const modelId = FoundationModels.JURASSIC2_ULTRA.modelId;
    const response = await invokeModel(TEXT_PROMPT, modelId);
    expectToBeANonEmptyString(response[0]);
  });
});
