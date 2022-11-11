/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import { makeCreateJobStep } from "../scenarios/basic/steps/create-job.js";

describe("create-job", () => {
  it("should call the createJob action", async () => {
    const createJob = vi.fn(async () => {});
    const actions = { createJob };

    process.env.JOB_NAME = "flight_etl_job";
    process.env.ROLE_NAME = "role_name";
    process.env.BUCKET_NAME = "bucket_name";
    process.env.PYTHON_SCRIPT_KEY = "flight_etl_job_script.py";

    const step = makeCreateJobStep(actions);
    await step({});

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

    const step = makeCreateJobStep(actions);
    const actual = await step({});
    expect(actual).toEqual({});
  });
});
