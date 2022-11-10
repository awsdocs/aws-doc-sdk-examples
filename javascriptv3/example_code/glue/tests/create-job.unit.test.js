/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import * as envVars from "../scenarios/basic/env.js";
import { makeCreateJobStep } from "../scenarios/basic/steps/create-job.js";

describe("create-job", () => {
  it("should call the createJob action", async () => {
    const createJob = vi.fn(async () => {});
    const actions = { createJob };
    const context = { envVars };

    const step = makeCreateJobStep(actions);
    await step(context);

    expect(createJob).toHaveBeenCalledWith(
      "flight_etl_job",
      expect.stringContaining(""),
      expect.stringContaining(""),
      "flight_etl_job_script.py"
    );
  });

  it("should return a context object", async () => {
    const createJob = vi.fn();
    const actions = { createJob };

    const context = { envVars: {} };

    const step = makeCreateJobStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
