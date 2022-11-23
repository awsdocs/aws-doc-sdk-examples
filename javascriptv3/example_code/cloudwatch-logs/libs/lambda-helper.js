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
} from "@aws-sdk/client-lambda";
import { DEFAULT_REGION } from "./constants.js";

const client = new LambdaClient({ region: DEFAULT_REGION });

/**
 *
 * @param {string} roleArn
 */
export const createFunction = (roleArn) => {
  const lambdaFunctionBuffer = readFileSync(`./tests/data/lambda-function.zip`);
  const command = new CreateFunctionCommand({
    Code: { ZipFile: lambdaFunctionBuffer },
    FunctionName: `cloudwatch-log-subscriber`,
    Role: roleArn,
    Architectures: [Architecture.arm64],
    Handler: "index.handler",
    PackageType: PackageType.Zip,
    Runtime: Runtime.nodejs16x,
  });

  return client.send(command);
};

export const deleteFunction = (functionName) => {
  const command = new DeleteFunctionCommand({ FunctionName: functionName });
  return client.send(command);
};
