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

import { dirnameFromMetaUrl } from "@aws-sdk-examples/libs/utils/util-fs.js";
import { DEFAULT_REGION } from "@aws-sdk-examples/libs/utils/util-aws-sdk.js";
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

/**
 *
 * @param {{ Outputs: import("@aws-sdk/client-cloudformation").Output[] }")}} param0
 * @returns
 */
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
    { StackName: stackName },
  );

  // eslint-disable-next-line @typescript-eslint/no-unsafe-member-access
  return getResourceNames(response.reason.Stacks[0]);
};

const emptyS3Bucket = async (bucketName) => {
  const client = new S3Client({ region: DEFAULT_REGION });
  const listCommand = new ListObjectsCommand({ Bucket: bucketName });
  const { Contents } = await client.send(listCommand);

  await Promise.all(
    Contents.map(({ Key }) =>
      client.send(new DeleteObjectCommand({ Bucket: bucketName, Key })),
    ),
  );
};

const deleteStack = async () => {
  const client = new CloudFormationClient({ region: DEFAULT_REGION });
  const command = new DeleteStackCommand({ StackName: stackName });
  await client.send(command);
  await waitUntilStackDeleteComplete(
    { client, maxWaitTime: fiveMinutesInSeconds },
    { StackName: stackName },
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
      await deleteTable("doc-example-database", "doc-example-csv").catch(
        console.error,
      );
      await deleteDatabase("doc-example-database");
      await deleteCrawler("s3-flight-data-crawler").catch(console.error);
      await deleteJob("flight_etl_job").catch(console.error);
      await deleteStack().catch(console.error);
    } catch (err) {
      console.error(err);
    }
  }, fiveMinutesInMs);

  const addPythonScriptToBucket = async () => {
    const client = new S3Client({ region: DEFAULT_REGION });
    const pyScriptPath = `${dirname}../../../../python/example_code/glue/flight_etl_job_script.py`;
    const pyScript = (await readFile(pyScriptPath)).toString("utf-8");
    const command = new PutObjectCommand({
      Bucket: bucketName,
      Key: "flight_etl_job_script.py",
      Body: pyScript,
    });

    await client.send(command);
  };

  const testCreateCrawler = async () => {
    await createCrawler(
      "s3-flight-data-crawler",
      roleName,
      "doc-example-database",
      "doc-example-",
      "s3://crawler-public-us-east-1/flight/2016/csv",
    );

    const crawler = await getCrawler("s3-flight-data-crawler");
    expect(crawler).toBeTruthy();
  };

  const testCreateJob = async (bucketName, roleName) => {
    await createJob(
      "flight_etl_job",
      roleName,
      bucketName,
      "flight_etl_job_script.py",
    );

    const job = await getJob("flight_etl_job");
    expect(job).toBeTruthy();
  };

  const testListJobs = async () => {
    const { JobNames } = await listJobs();
    expect(JobNames).toContain("flight_etl_job");
  };

  const testStartCrawler = async () => {
    await startCrawler("s3-flight-data-crawler");
    await waitForCrawler(getCrawler, "s3-flight-data-crawler");
  };

  const testGetDatabases = async () => {
    const { DatabaseList } = await getDatabases();
    expect(DatabaseList[0].Name).toBe("doc-example-database");
  };

  const testGetDatabase = async () => {
    const {
      Database: { Name },
    } = await getDatabase("doc-example-database");
    expect(Name).toBe("doc-example-database");
  };

  const testGetTables = async () => {
    const { TableList } = await getTables("doc-example-database");
    expect(TableList[0].Name).toBe("doc-example-csv");
  };

  const testStartJobRun = async (bucketName) => {
    const { JobRunId } = await startJobRun(
      "flight_etl_job",
      "doc-example-database",
      "doc-example-csv",
      bucketName,
    );
    await waitForJobRun(getJobRun, "flight_etl_job", JobRunId);
  };

  const testGetJobRuns = async () => {
    const { JobRuns } = await getJobRuns("flight_etl_job");
    expect(JobRuns[0].JobName).toBe("flight_etl_job");
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
    { timeout: fiveMinutesInMs * 5 },
  );
});
