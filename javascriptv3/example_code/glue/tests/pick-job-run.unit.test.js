/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import { mockPrompter } from "./mock-prompter.js";
import { makePickJobRunStep } from "../scenarios/basic/steps/pick-job-run.js";

describe("pick-job-run", () => {
  it("should call getJobRun for a selected job run", async () => {
    const getJobRun = vi.fn(async () => ({}));
    const getJobRuns = vi.fn(async () => ({ JobRuns: [{ Id: "jobRun1" }] }));
    const actions = { getJobRun, getJobRuns };
    const context = {
      selectedJobName: "job1",
      prompter: mockPrompter({ jobRunId: "jobRun1" }),
    };

    const step = makePickJobRunStep(actions);
    await step(context);
    expect(getJobRuns).toBeCalledWith("job1");
    expect(getJobRun).toHaveBeenCalledWith("job1", "jobRun1")
  });

  it("should return a context object", async () => {
    const getJobRun = vi.fn(async () => ({}));
    const getJobRuns = vi.fn(async () => ({ JobRuns: [{ Id: "jobRun1" }] }));
    const actions = { getJobRun, getJobRuns };

    const context = { envVars: {} };

    const step = makePickJobRunStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
