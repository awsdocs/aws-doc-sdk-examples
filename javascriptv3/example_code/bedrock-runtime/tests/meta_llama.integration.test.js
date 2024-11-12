// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, expect, it, vi } from "vitest";

/**
 * Integration tests for:
 * - models/meta/llama3/*.js
 */

describe("Running the Llama3 InvokeModel quickstart", () => {
  it("should run and log the model's response", async () => {
    const log = vi.spyOn(console, "log").mockImplementation(() => {});
    await import("../models/metaLlama/llama3/invoke_model_quickstart.js");
    expect(log).toHaveBeenCalledTimes(1);
    log.mockRestore();
  });
});

describe("Running the Llama3 InvokeModelWithResponseStream quickstart", () => {
  it("should run and log the model's response", async () => {
    const write = vi
      .spyOn(process.stdout, "write")
      .mockImplementation(() => {});
    await import(
      "../models/metaLlama/llama3/invoke_model_with_response_stream_quickstart.js"
    );
    expect(write).toHaveBeenCalled();
    write.mockRestore();
  });
});
