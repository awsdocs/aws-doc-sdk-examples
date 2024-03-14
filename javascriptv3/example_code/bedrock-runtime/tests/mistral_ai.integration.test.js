// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {describe, it} from "vitest";
import {FoundationModels} from "../foundation_models.js";
import {expectToBeANonEmptyString} from "./test_tools.js";
import {invokeModel} from "../mistral_ai/mistral.js";


const TEXT_PROMPT = "Hello, this is a test prompt";

describe("Invoke Mistral 7B", () => {
    it("should return a response", async () => {
        const modelId = FoundationModels.MISTRAL_7B.modelId;
        const response = await invokeModel(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response[0]);
    })
});

describe("Invoke Mixtral 8x7B", () => {
    it("should return a response", async () => {
        const modelId = FoundationModels.MIXTRAL_8X7B.modelId;
        const response = await invokeModel(TEXT_PROMPT, modelId);
        expectToBeANonEmptyString(response[0]);
    })
});
