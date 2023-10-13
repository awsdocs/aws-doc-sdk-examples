/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { DescribeSubscriptionFiltersCommand } from "@aws-sdk/client-cloudwatch-logs";
import { LambdaClient, waitUntilFunctionUpdated } from "@aws-sdk/client-lambda";
import { describe, it, beforeAll, afterAll, expect } from "vitest";

import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";
import { setEnv } from "@aws-sdk-examples/libs/utils/util-node.js";

import {
  addPermissionLogsInvokeFunction,
  createFunction,
  deleteFunction,
} from "../libs/lambda-helper.js";
import {
  attachRolePolicy,
  createLambdaRole,
  deleteRole,
  detachRolePolicy,
} from "../libs/iam-helper.js";
import { DEFAULT_REGION, LAMBDA_EXECUTION_POLICY } from "../libs/constants.js";
import { client } from "../libs/client.js";

const testTimeout = 60000;

const initializeLambdaFunction = async ({ funcName, roleName }) => {
  const roleArn = await createLambdaRole(roleName);
  await attachRolePolicy(roleName, LAMBDA_EXECUTION_POLICY);

  const { FunctionArn } = await retry(
    { intervalInMs: 2000, maxRetries: 20 },
    () => createFunction(funcName, roleArn),
  );
  setEnv("CLOUDWATCH_LOGS_DESTINATION_ARN", FunctionArn);
  return { functionArn: FunctionArn };
};

const createLogGroup = async (name) => {
  setEnv("CLOUDWATCH_LOGS_LOG_GROUP", name);

  const mod = await import("../actions/create-log-group.js");
  return await mod.default;
};

const deleteLogGroup = async () => {
  const mod = await import("../actions/delete-log-group.js");
  await mod.default;
};

const testCreateFilter = async (name, pattern) => {
  setEnv("CLOUDWATCH_LOGS_FILTER_NAME", name);
  setEnv("CLOUDWATCH_LOGS_FILTER_PATTERN", pattern);
  const putSubFilterMod = await import("../actions/put-subscription-filter.js");
  await putSubFilterMod.default;

  const descSubFiltersMod = await import(
    "../actions/describe-subscription-filters.js"
  );
  const result = await descSubFiltersMod.default;

  expect(result?.subscriptionFilters[0].filterName).toBe(name);
};

const testDeleteFilter = async () => {
  const deleteSubFilterMod = await import(
    "../actions/delete-subscription-filter.js"
  );

  await deleteSubFilterMod.default;

  await retry({ intervalInMs: 2000, maxRetries: 20 }, async () => {
    const command = new DescribeSubscriptionFiltersCommand({
      logGroupName: process.env.CLOUDWATCH_LOGS_LOG_GROUP,
      limit: 1,
    });

    const { subscriptionFilters } = await client.send(command);

    expect(subscriptionFilters.length).toBe(0);
  });
};

describe("put-subscription-filter", () => {
  const lambdaRoleName = "SubscriptionFilterLambdaRole";
  const lambdaFuncName = "SubscriptionFilterLambdaFunc";
  const logGroupName = "SubscriptionFilterLogGroup";
  const subscriptionFilterName = "SubscriptionFilter";
  const subscriptionFilterPattern = "ERROR";

  beforeAll(async () => {
    try {
      await initializeLambdaFunction({
        funcName: lambdaFuncName,
        roleName: lambdaRoleName,
      });

      await createLogGroup(logGroupName);

      await addPermissionLogsInvokeFunction(lambdaFuncName, logGroupName);
      await waitUntilFunctionUpdated(
        { client: new LambdaClient({ region: DEFAULT_REGION }) },
        { FunctionName: lambdaFuncName },
      );
    } catch (err) {
      console.error(err);
    }
  }, testTimeout);

  afterAll(async () => {
    try {
      await deleteFunction(lambdaFuncName);
      await detachRolePolicy(lambdaRoleName, LAMBDA_EXECUTION_POLICY);
      await deleteRole(lambdaRoleName);
      await deleteLogGroup();
    } catch (err) {
      console.error(err);
    }
  }, testTimeout);

  it(
    "should create a subscription filter and delete it",
    async () => {
      await testCreateFilter(subscriptionFilterName, subscriptionFilterPattern);
      await testDeleteFilter();
    },
    testTimeout,
  );
});
