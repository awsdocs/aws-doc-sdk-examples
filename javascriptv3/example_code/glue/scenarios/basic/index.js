/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import inquirer from "inquirer";
import { pipe, andThen } from "ramda";
import { config } from "dotenv";

import { deleteDatabase } from "../../actions/delete-database.js";
import { createCrawler } from "../../actions/create-crawler.js";
import { createJob } from "../../actions/create-job.js";
import { deleteJob } from "../../actions/delete-job.js";
import { deleteTable } from "../../actions/delete-table.js";
import { getCrawler } from "../../actions/get-crawler.js";
import { getDatabase } from "../../actions/get-database.js";
import { getDatabases } from "../../actions/get-databases.js";
import { getJobRun } from "../../actions/get-job-run.js";
import { getJobRuns } from "../../actions/get-job-runs.js";
import { getTables } from "../../actions/get-tables.js";
import { listJobs } from "../../actions/list-jobs.js";
import { startCrawler } from "../../actions/start-crawler.js";
import { startJobRun } from "../../actions/start-job-run.js";
import { s3ListObjects } from "../../non-glue-actions/s3-list-objects.js";

import { log } from "./log.js";
import { cleanUpCrawlerStep } from "./steps/clean-up-crawler.js";
import { makeCleanUpDatabasesStep } from "./steps/clean-up-databases.js";
import { makeCleanUpJobsStep } from "./steps/clean-up-jobs.js";
import { makeCleanUpTablesStep } from "./steps/clean-up-tables.js";
import { makeCreateJobStep } from "./steps/create-job.js";
import { deleteStackReminder } from "./steps/delete-stack-reminder.js";
import { makeGetDatabaseStep } from "./steps/get-database.js";
import { makeGetTablesStep } from "./steps/get-tables.js";
import { makeCreateCrawlerStep } from "./steps/create-crawler.js";
import { makePickJobRunStep } from "./steps/pick-job-run.js";
import { makePickJobStep } from "./steps/pick-job.js";
import { makeStartCrawlerStep } from "./steps/start-crawler.js";
import { makeStartJobRunStep } from "./steps/start-job-run.js";
import { validateEnv } from "./steps/validate-env.js";
import { makeValidatePythonScriptStep } from "./steps/validate-python-script.js";

config();

const run = pipe(
  validateEnv,
  andThen(makeValidatePythonScriptStep({ s3ListObjects })),
  andThen(makeCreateCrawlerStep({ createCrawler, getCrawler })),
  andThen(makeStartCrawlerStep({ getCrawler, startCrawler })),
  andThen(makeGetDatabaseStep({ getDatabase })),
  andThen(makeGetTablesStep({ getTables })),
  andThen(makeCreateJobStep({ createJob })),
  andThen(makeStartJobRunStep({ startJobRun, getJobRun })),
  andThen(makePickJobStep({ listJobs })),
  andThen(makePickJobRunStep({ getJobRun, getJobRuns })),
  andThen(makeCleanUpJobsStep({ listJobs, deleteJob })),
  andThen(makeCleanUpTablesStep({ getTables, deleteTable })),
  andThen(makeCleanUpDatabasesStep({ getDatabases, deleteDatabase })),
  andThen(cleanUpCrawlerStep),
  andThen(deleteStackReminder)
);

try {
  await run({
    prompter: inquirer,
  });
} catch (err) {
  if (err.isTtyError) {
    log("Prompt could not be rendered.", { type: "error" });
  } else {
    log(err, { type: "error" });
  }
}
