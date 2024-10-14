// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { z } from "zod";
import { describe, it, expect } from "vitest";

import { getFoundationModel } from "../actions/get-foundation-model.js";

const ExpectedSchema = z.object({
  modelId: z.string(),
  modelName: z.string(),
});

describe("get-foundation-model", () => {
  it("should return the model' details", async () => {
    const modelDetails = await getFoundationModel();
    expect(modelDetails).not.toBeNull();

    const isModelDetails = ExpectedSchema.safeParse(modelDetails).success;
    expect(isModelDetails).toBe(true);
  });
});
