/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, beforeAll, afterAll, expect } from "vitest";
import {
  IAMClient,
  CreateRoleCommand,
  AttachRolePolicyCommand,
  DeleteRoleCommand,
  DetachRolePolicyCommand,
  waitUntilRoleExists,
} from "@aws-sdk/client-iam";

import { log } from "@aws-sdk-examples/libs/utils/util-log.js";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";
import { DEFAULT_REGION } from "@aws-sdk-examples/libs/utils/util-aws-sdk.js";

import {
  waitForFunctionActive,
  waitForFunctionUpdated,
} from "../../lambda/waiters/index.js";
import { createFunction } from "../actions/create-function.js";
import { deleteFunction } from "../actions/delete-function.js";
import { getFunction } from "../actions/get-function.js";
import { invoke } from "../actions/invoke.js";
import { listFunctions } from "../actions/list-functions.js";
import { updateFunctionCode } from "../actions/update-function-code.js";
import { updateFunctionConfiguration } from "../actions/update-function-configuration.js";
import { helloLambda } from "../hello.js";

describe("Creating, getting, invoking, listing, updating, and deleting", () => {
  const iamClient = new IAMClient({ region: DEFAULT_REGION });
  const roleName = "test-lambda-actions-role-name";
  const rolePolicyArn =
    "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole";
  const funcName = "func-math";
  let roleArn;

  beforeAll(async () => {
    const createRoleCommand = new CreateRoleCommand({
      AssumeRolePolicyDocument: JSON.stringify({
        Version: "2012-10-17",
        Statement: [
          {
            Effect: "Allow",
            Principal: {
              Service: "lambda.amazonaws.com",
            },
            Action: "sts:AssumeRole",
          },
        ],
      }),
      RoleName: roleName,
    });
    try {
      const response = await iamClient.send(createRoleCommand);
      roleArn = response.Role ? response.Role.Arn : null;
      await waitUntilRoleExists(
        {
          client: iamClient,
          maxWaitTime: 15,
        },
        { RoleName: roleName },
      );
      const attachRolePolicyCommand = new AttachRolePolicyCommand({
        PolicyArn: rolePolicyArn,
        RoleName: roleName,
      });
      await iamClient.send(attachRolePolicyCommand);
    } catch (err) {
      log(err);
      throw err;
    }
  });

  afterAll(async () => {
    try {
      const detachRolePolicyCommand = new DetachRolePolicyCommand({
        PolicyArn: rolePolicyArn,
        RoleName: roleName,
      });
      await iamClient.send(detachRolePolicyCommand);
      const deleteRoleCommand = new DeleteRoleCommand({ RoleName: roleName });
      await iamClient.send(deleteRoleCommand);
    } catch (err) {
      log(err);
      throw err;
    }

    try {
      // Ensure function gets deleted even if the test fails
      // before the deletion step.
      await deleteFunction(funcName);
    } catch (err) {
      log(err);
    }
  });

  const testCreateFunction = async () => {
    // A role goes into a busy state after attaching a policy
    // and there's no explicit waiter available for this.
    await retry({ intervalInMs: 2000, maxRetries: 15 }, () =>
      createFunction(funcName, roleArn),
    );

    const response = await retry({ intervalInMs: 2000, maxRetries: 15 }, () =>
      getFunction(funcName),
    );
    expect(response.Configuration.FunctionName).toBe(funcName);
  };

  const testHello = async () => {
    const funcs = await helloLambda();
    expect(funcs).toContain(funcName);
  };

  const testInvokeFunction = async () => {
    // Verify 'invoke-function' works.
    const { result } = await invoke(funcName, "1");
    expect(result).toBe("2");
  };

  const testDeleteFunction = async () => {
    try {
      await deleteFunction(funcName);
      await getFunction(funcName);
    } catch (err) {
      expect(err.name).toBe("ResourceNotFoundException");
    }
  };

  const testListFunctions = async () => {
    const getFunctionNames = async () => {
      const response = await listFunctions();
      return response.Functions.map((func) => func.FunctionName);
    };

    const functionNames = await getFunctionNames();
    expect(functionNames).toContain(funcName);
  };

  const testUpdateFunction = async () => {
    await updateFunctionCode(funcName, "func-update");
    await waitForFunctionUpdated({ FunctionName: funcName });
    const { result } = await invoke(funcName, ["add", "1", "1"]);
    expect(result).toBe("2");
  };

  // Ideally these should be separate tests, but since we're creating
  // real AWS resources, this saves on testing time and possibly
  // cost.
  it("are all handled by this one test", async () => {
    await testCreateFunction();
    await waitForFunctionActive({ FunctionName: funcName });
    await testHello();
    await updateFunctionConfiguration(funcName);
    await retry({ intervalInMs: 2000, maxRetries: 15 }, testInvokeFunction);
    await testListFunctions();
    await testUpdateFunction();
    await testDeleteFunction();
  });
});
