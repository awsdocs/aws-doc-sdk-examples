// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/**
 * @typedef {() => Promise<import('@aws-sdk/client-glue').ListJobsCommandOutput>} listJobs
 */

/**
 * @typedef {{ prompter: { prompt: () => Promise<{jobName: string}> } }} Context
 */

const makePickJobStep =
  (/** @type { {listJobs: listJobs } } */ { listJobs }) =>
  async (/** @type { Context } */ context) => {
    const { JobNames } = await listJobs();

    if (JobNames.length > 0) {
      const { jobName } = await context.prompter.prompt({
        name: "jobName",
        type: "list",
        message: "Select a job to see job runs.",
        choices: JobNames,
      });

      return { ...context, selectedJobName: jobName };
    }

    return { ...context };
  };

export { makePickJobStep };
