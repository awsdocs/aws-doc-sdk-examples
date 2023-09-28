/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { readFileSync } from "fs";
import {
  CreateFunctionCommand,
  Architecture,
  PackageType,
  Runtime,
  LambdaClient,
  DeleteFunctionCommand,
  AddPermissionCommand,
} from "@aws-sdk/client-lambda";
import {
  CloudWatchLogsClient,
  paginateDescribeLogGroups,
} from "@aws-sdk/client-cloudwatch-logs";

const lambdaClient = new LambdaClient({});
const cloudWatchLogsClient = new CloudWatchLogsClient({});

/**
 *
 * @param {string} roleArn
 */
export const createFunction = async (name, roleArn) => {
  const lambdaFunctionBuffer = readFileSync(`./tests/data/lambda-function.zip`);
  const command = new CreateFunctionCommand({
    Code: { ZipFile: lambdaFunctionBuffer },
    FunctionName: name,
    Role: roleArn,
    Architectures: [Architecture.arm64],
    Handler: "index.handler",
    PackageType: PackageType.Zip,
    Runtime: Runtime.nodejs16x,
  });

  return await lambdaClient.send(command);
};

export const deleteFunction = async (functionName) => {
  const command = new DeleteFunctionCommand({ FunctionName: functionName });

  try {
    return await lambdaClient.send(command);
  } catch (err) {
    console.error(err);
  }
};

export const addPermissionLogsInvokeFunction = async (
  functionName,
  logGroupName,
) => {
  const logGroupPaginator = paginateDescribeLogGroups(
    { client: cloudWatchLogsClient },
    {},
  );

  let logGroup;

  for await (const page of logGroupPaginator) {
    if (
      (logGroup = page.logGroups.find((lg) => lg.logGroupName === logGroupName))
    ) {
      break;
    }
  }

  if (!logGroup) {
    throw new Error("No matching log group found.");
  }

  const command = new AddPermissionCommand({
    FunctionName: functionName,
    StatementId: `${functionName}${Date.now()}`,
    Action: "lambda:InvokeFunction",
    Principal: "logs.amazonaws.com",
    SourceArn: logGroup.arn,
  });

  return lambdaClient.send(command);
};
