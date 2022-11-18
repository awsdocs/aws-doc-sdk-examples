/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { readFile } from "fs/promises";
import { describe, it, expect, beforeAll, afterAll } from "vitest";
import { config } from "dotenv";
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

config();

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
      await deleteTable(
        process.env.DATABASE_NAME,
        process.env.TABLE_NAME
      ).catch(console.error);
      await deleteDatabase(process.env.DATABASE_NAME);
      await deleteCrawler(process.env.CRAWLER_NAME).catch(console.error);
      await deleteJob(process.env.JOB_NAME).catch(console.error);
      await deleteStack().catch(console.error);
    } catch (err) {
      console.error(err);
    }
  }, fiveMinutesInMs);

  const addPythonScriptToBucket = async () => {
    const client = new S3Client({ region: DEFAULT_REGION });
    const pyScriptPath = `${dirname}../../../../python/example_code/glue/${process.env.PYTHON_SCRIPT_KEY}`;
    const pyScript = (await readFile(pyScriptPath)).toString("utf-8");
    const command = new PutObjectCommand({
      Bucket: bucketName,
      Key: process.env.PYTHON_SCRIPT_KEY,
      Body: pyScript,
    });

    await client.send(command);
  };

  const testCreateCrawler = async () => {
    await createCrawler(
      process.env.CRAWLER_NAME,
      roleName,
      process.env.DATABASE_NAME,
      process.env.TABLE_PREFIX,
      process.env.S3_TARGET_PATH
    );

    const crawler = await getCrawler(process.env.CRAWLER_NAME);
    expect(crawler).toBeTruthy();
  };

  const testCreateJob = async (bucketName, roleName) => {
    await createJob(
      process.env.JOB_NAME,
      roleName,
      bucketName,
      process.env.PYTHON_SCRIPT_KEY
    );

    const job = await getJob(process.env.JOB_NAME);
    expect(job).toBeTruthy();
  };

  const testListJobs = async () => {
    const { JobNames } = await listJobs();
    expect(JobNames).toContain(process.env.JOB_NAME);
  };

  const testStartCrawler = async () => {
    await startCrawler(process.env.CRAWLER_NAME);
    await waitForCrawler(getCrawler, process.env.CRAWLER_NAME);
  };

  const testGetDatabases = async () => {
    const { DatabaseList } = await getDatabases();
    expect(DatabaseList[0].Name).toBe(process.env.DATABASE_NAME);
  };

  const testGetDatabase = async () => {
    const {
      Database: { Name },
    } = await getDatabase(process.env.DATABASE_NAME);
    expect(Name).toBe(process.env.DATABASE_NAME);
  };

  const testGetTables = async () => {
    const { TableList } = await getTables(process.env.DATABASE_NAME);
    expect(TableList[0].Name).toBe(process.env.TABLE_NAME);
  };

  const testStartJobRun = async (bucketName) => {
    const { JobRunId } = await startJobRun(
      process.env.JOB_NAME,
      process.env.DATABASE_NAME,
      process.env.TABLE_NAME,
      bucketName
    );
    await waitForJobRun(getJobRun, process.env.JOB_NAME, JobRunId);
  };

  const testGetJobRuns = async () => {
    const { JobRuns } = await getJobRuns(process.env.JOB_NAME);
    expect(JobRuns[0].JobName).toBe(process.env.JOB_NAME);
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
