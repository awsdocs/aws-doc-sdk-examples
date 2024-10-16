// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { z } from "zod";
import { describe, it, expect } from "vitest";

import { listFoundationModels } from "../actions/list-foundation-models.js";

const ExpectedSchema = z.object({
  modelId: z.string(),
  modelName: z.string(),
});

describe("list-foundation-models", () => {
  it("should return model summaries", async () => {
    const modelSummaries = await listFoundationModels();
    expect(modelSummaries.length).toBeGreaterThan(0);

    const isModelSummary = ExpectedSchema.safeParse(modelSummaries[0]).success;
    expect(isModelSummary).toBe(true);
  });
});
