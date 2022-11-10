/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

import * as envVars from "../scenarios/basic/env.js";
import { makeStartJobRunStep } from "../scenarios/basic/steps/start-job-run.js";
import { mockPrompter } from "./mock-prompter.js";

describe("start-job-run", async () => {
  it("should call startJobRun with the job name from the environment variables", async () => {
    const startJobRun = vi.fn(async () => ({}));
    const getJobRun = vi.fn(async () => ({
      JobRun: { JobRunState: "SUCCEEDED" },
    }));
    const actions = { startJobRun, getJobRun };

    const context = { envVars, prompter: mockPrompter({ shouldOpen: false }) };

    const step = makeStartJobRunStep(actions);
    await step(context);

    expect(startJobRun).toHaveBeenCalledWith(
      envVars.JOB_NAME,
      envVars.DATABASE_NAME,
      envVars.TABLE_NAME,
      envVars.BUCKET_NAME
    );
  });

  it("should call prompt the user about opening a browser if the job succeeds", async () => {
    const startJobRun = vi.fn(async () => ({}));
    const getJobRun = vi.fn(async () => ({
      JobRun: { JobRunState: "SUCCEEDED" },
    }));
    const actions = { startJobRun, getJobRun };
    const prompter = mockPrompter({ shouldOpen: false });
    const context = { envVars, prompter };

    const step = makeStartJobRunStep(actions);
    await step(context);

    expect(prompter.prompt).toHaveBeenCalledWith({
      message: "Open the output bucket in your browser?",
      name: "shouldOpen",
      type: "confirm",
    });
  });

  it("should throw an error if the job run failed", async () => {
    const startJobRun = vi.fn(async () => ({}));
    const getJobRun = vi.fn(async () => ({
      JobRun: { JobRunState: "FAILED", ErrorMessage: "Deliberate Failure" },
    }));
    const actions = { startJobRun, getJobRun };

    const context = { envVars, prompter: mockPrompter({ shouldOpen: false }) };

    const step = makeStartJobRunStep(actions);
    const error = new Error("Job FAILED. Error: Deliberate Failure");
    error.name = "Error";
    return expect(step(context)).rejects.toEqual(error);
  });

  it("should return a context object", async () => {
    const startJobRun = vi.fn(async () => ({}));
    const getJobRun = vi.fn(async () => ({
      JobRun: { JobRunState: "SUCCEEDED" },
    }));
    const actions = { startJobRun, getJobRun };

    const context = { envVars, prompter: mockPrompter({ shouldOpen: false }) };

    const step = makeStartJobRunStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
