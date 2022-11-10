/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { readFile } from "fs/promises";
import { describe, it, expect, beforeAll, afterAll } from "vitest";
import {
  CloudFormationClient,
  CreateStackCommand,
  DeleteStackCommand,
  waitUntilStackCreateComplete,
  waitUntilStackDeleteComplete,
} from "@aws-sdk/client-cloudformation";
import {
  DeleteObjectCommand,
  ListObjectsCommand,
  PutObjectCommand,
  S3Client,
} from "@aws-sdk/client-s3";

import { dirnameFromMetaUrl } from "../../libs/utils/util-fs.js";
import { DEFAULT_REGION } from "../../libs/utils/util-aws-sdk.js";
import * as envVars from "../scenarios/basic/env.js";
import { createCrawler } from "../actions/create-crawler.js";
import { getCrawler } from "../actions/get-crawler.js";
import { deleteCrawler } from "../actions/delete-crawler.js";
import { createJob } from "../actions/create-job.js";
import { getJob } from "../actions/get-job.js";
import { deleteJob } from "../actions/delete-job.js";
import { startCrawler } from "../actions/start-crawler.js";
import { waitForCrawler } from "../scenarios/basic/steps/start-crawler.js";
import { startJobRun } from "../actions/start-job-run.js";
import { waitForJobRun } from "../scenarios/basic/steps/start-job-run.js";
import { getJobRun } from "../actions/get-job-run.js";
import { deleteTable } from "../actions/delete-table.js";
import { deleteDatabase } from "../actions/delete-database.js";
import { getDatabases } from "../actions/get-databases.js";
import { getDatabase } from "../actions/get-database.js";
import { getJobRuns } from "../actions/get-job-runs.js";
import { listJobs } from "../actions/list-jobs.js";
import { getTables } from "../actions/get-tables.js";

const dirname = dirnameFromMetaUrl(import.meta.url);
const cdkAppPath = `${dirname}../../../../resources/cdk/glue_role_bucket/setup.yaml`;
const stackName = `glue-test-stack-${Date.now()}`;
const fiveMinutesInMs = 5 * 60 * 1000; // 5 Minutes
const fiveMinutesInSeconds = fiveMinutesInMs / 1000;

const getResourceNames = ({ Outputs }) =>
  Outputs.reduce((resourceNameMap, nextOutput) => {
    return {
      ...resourceNameMap,
      [nextOutput.OutputKey]: nextOutput.OutputValue,
    };
  }, {});

const createStack = async () => {
  const client = new CloudFormationClient({ region: DEFAULT_REGION });
  const templateBody = (await readFile(cdkAppPath)).toString("utf-8");
  const command = new CreateStackCommand({
    StackName: stackName,
    TemplateBody: templateBody,
    Capabilities: ["CAPABILITY_NAMED_IAM"],
  });
  await client.send(command);
  const response = await waitUntilStackCreateComplete(
    { client, maxWaitTime: fiveMinutesInSeconds },
    { StackName: stackName }
  );

  return getResourceNames(response.reason.Stacks[0]);
};

const emptyS3Bucket = async (bucketName) => {
  const client = new S3Client({ region: DEFAULT_REGION });
  const listCommand = new ListObjectsCommand({ Bucket: bucketName });
  const { Contents } = await client.send(listCommand);

  await Promise.all(
    Contents.map(({ Key }) =>
      client.send(new DeleteObjectCommand({ Bucket: bucketName, Key }))
    )
  );
};

const deleteStack = async () => {
  const client = new CloudFormationClient({ region: DEFAULT_REGION });
  const command = new DeleteStackCommand({ StackName: stackName });
  await client.send(command);
  await waitUntilStackDeleteComplete(
    { client, maxWaitTime: fiveMinutesInSeconds },
    { StackName: stackName }
  );
};

describe("actions", () => {
  let roleName, bucketName;

  beforeAll(async () => {
    try {
      const { BucketName, RoleName } = await createStack();
      roleName = RoleName;
      bucketName = BucketName;
    } catch (err) {
      console.error(err);
    }
  }, fiveMinutesInMs);

  afterAll(async () => {
    try {
      await emptyS3Bucket(bucketName).catch(console.error);
      await deleteTable(envVars.DATABASE_NAME, envVars.TABLE_NAME).catch(
        console.error
      );
      await deleteDatabase(envVars.DATABASE_NAME);
      await deleteCrawler(envVars.CRAWLER_NAME).catch(console.error);
      await deleteJob(envVars.JOB_NAME).catch(console.error);
      await deleteStack().catch(console.error);
    } catch (err) {
      console.error(err);
    }
  }, fiveMinutesInMs);

  const addPythonScriptToBucket = async () => {
    const client = new S3Client({ region: DEFAULT_REGION });
    const pyScriptPath = `${dirname}../../../../python/example_code/glue/${envVars.PYTHON_SCRIPT_KEY}`;
    const pyScript = (await readFile(pyScriptPath)).toString("utf-8");
    const command = new PutObjectCommand({
      Bucket: bucketName,
      Key: envVars.PYTHON_SCRIPT_KEY,
      Body: pyScript,
    });

    await client.send(command);
  };

  const testCreateCrawler = async () => {
    await createCrawler(
      envVars.CRAWLER_NAME,
      roleName,
      envVars.DATABASE_NAME,
      envVars.TABLE_PREFIX,
      envVars.S3_TARGET_PATH
    );

    const crawler = await getCrawler(envVars.CRAWLER_NAME);
    expect(crawler).toBeTruthy();
  };

  const testCreateJob = async (bucketName, roleName) => {
    await createJob(
      envVars.JOB_NAME,
      roleName,
      bucketName,
      envVars.PYTHON_SCRIPT_KEY
    );

    const job = await getJob(envVars.JOB_NAME);
    expect(job).toBeTruthy();
  };

  const testListJobs = async () => {
    const { JobNames } = await listJobs();
    expect(JobNames).toContain(envVars.JOB_NAME);
  };

  const testStartCrawler = async () => {
    await startCrawler(envVars.CRAWLER_NAME);
    await waitForCrawler(getCrawler, envVars.CRAWLER_NAME);
  };

  const testGetDatabases = async () => {
    const { DatabaseList } = await getDatabases();
    expect(DatabaseList[0].Name).toBe(envVars.DATABASE_NAME);
  };

  const testGetDatabase = async () => {
    const {
      Database: { Name },
    } = await getDatabase(envVars.DATABASE_NAME);
    expect(Name).toBe(envVars.DATABASE_NAME);
  };

  const testGetTables = async () => {
    const { TableList } = await getTables(envVars.DATABASE_NAME);
    expect(TableList[0].Name).toBe(envVars.TABLE_NAME);
  };

  const testStartJobRun = async (bucketName) => {
    const { JobRunId } = await startJobRun(
      envVars.JOB_NAME,
      envVars.DATABASE_NAME,
      envVars.TABLE_NAME,
      bucketName
    );
    await waitForJobRun(getJobRun, envVars.JOB_NAME, JobRunId);
  };

  const testGetJobRuns = async () => {
    const { JobRuns } = await getJobRuns(envVars.JOB_NAME);
    expect(JobRuns[0].JobName).toBe(envVars.JOB_NAME);
  };

  it(
    "should run",
    async () => {
      await addPythonScriptToBucket();
      await testCreateCrawler();
      await testCreateJob(bucketName, roleName);
      await testListJobs();
      await testStartCrawler();
      await testGetDatabases();
      await testGetDatabase();
      await testGetTables();
      await testStartJobRun(bucketName);
      await testGetJobRuns();
    },
    { timeout: fiveMinutesInMs * 5 }
  );
});
