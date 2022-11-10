/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { it, describe, vi, expect } from "vitest";
import { makeCleanUpJobsStep } from "../scenarios/basic/steps/clean-up-jobs.js";
import { mockPrompter } from "./mock-prompter.js";

describe("clean-up-jobs", () => {
  it("should not prompt to delete, or delete, any jobs if none exist", async () => {
    const listJobs = vi.fn(async () => ({ JobNames: [] }));
    const deleteJob = vi.fn(async () => {});
    const prompter = mockPrompter();
    const cleanUpJobsStep = makeCleanUpJobsStep({
      listJobs,
      deleteJob,
    });

    await cleanUpJobsStep({ prompter });
    expect(deleteJob).not.toHaveBeenCalled();
    expect(prompter.prompt).not.toHaveBeenCalled();
  });

  it("should prompt to delete, and delete, any jobs if they are selected", async () => {
    const listJobs = vi.fn(async () => ({ JobNames: ["job1"] }));
    const deleteJob = vi.fn(async () => {});
    const prompter = mockPrompter({ selectedJobNames: ["job1"] });
    const cleanUpJobsStep = makeCleanUpJobsStep({
      listJobs,
      deleteJob,
    });

    await cleanUpJobsStep({ prompter });
    expect(deleteJob).toHaveBeenCalledWith("job1");
    expect(prompter.prompt).toHaveBeenCalled();
  });

  it("should prompt to delete, but not delete, if no jobs are selected", async () => {
    const listJobs = vi.fn(async () => ({ JobNames: ["job1"] }));
    const deleteJob = vi.fn(async () => {});
    const prompter = mockPrompter({ selectedJobNames: [] });
    const cleanUpJobsStep = makeCleanUpJobsStep({
      listJobs,
      deleteJob,
    });

    await cleanUpJobsStep({ prompter });
    expect(deleteJob).not.toHaveBeenCalledWith("job1");
    expect(prompter.prompt).toHaveBeenCalled();
  });

  it("should return a context object", async () => {
    const listJobs = vi.fn(async () => ({ JobNames: [] }));
    const deleteJob = vi.fn(async () => {});
    const actions = { listJobs, deleteJob };

    const context = { envVars: {} };

    const step = makeCleanUpJobsStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
