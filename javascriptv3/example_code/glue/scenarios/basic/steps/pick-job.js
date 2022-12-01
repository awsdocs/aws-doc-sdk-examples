/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */


const makePickJobStep =
  ({ listJobs }) =>
  async (context) => {
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
