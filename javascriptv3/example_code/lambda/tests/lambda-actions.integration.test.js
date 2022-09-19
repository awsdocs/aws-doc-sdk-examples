/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, beforeAll, afterAll, jest, expect } from "@jest/globals";
import { andThen, compose, map, path, prop } from "ramda";
import { createRole } from "../../iam/actions/create-role.js";
import { log } from "../../libs/utils/util-log.js";
import { attachRolePolicy } from "../../iam/actions/attach-role-policy.js";
import { detachRolePolicy } from "../../iam/actions/detach-role-policy.js";
import { deleteRole } from "../../iam/actions/delete-role.js";
import { waitForRole } from "../../iam/waiters/index.js";
import {
  waitForFunctionActive,
  waitForFunctionUpdated,
} from "../../lambda/waiters/index.js";
import { createFunction } from "../actions/create-function.js";
import { deleteFunction } from "../actions/delete-function.js";
import { retry } from "../../libs/utils/util-timers.js";
import { getFunction } from "../actions/get-function.js";
import { invoke } from "../actions/invoke.js";
import { listFunctions } from "../actions/list-functions.js";
import { updateFunctionCode } from "../actions/update-function-code.js";
import { updateFunctionConfiguration } from "../actions/update-function-configuration.js";

jest.setTimeout(120000);

const retryOver30Seconds = retry({ interval: 2000, maxRetries: 15 });

describe("Creating, getting, invoking, listing, updating, and deleting", () => {
  const roleName = "test-lambda-actions-role-name";
  const rolePolicyArn =
    "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole";
  const funcName = "func-math";
  let roleArn;

  beforeAll(async () => {
    try {
      const response = await createRole({
        AssumeRolePolicyDocument: parseString({
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
      roleArn = path(["Role", "Arn"], response);
      await waitForRole({ RoleName: roleName });
      await attachRolePolicy(roleName, rolePolicyArn);
    } catch (err) {
      log(err);
      throw err;
    }
  });

  afterAll(async () => {
    try {
      await detachRolePolicy(roleName, rolePolicyArn);
      await deleteRole(roleName);
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
    await retryOver30Seconds(() => createFunction(funcName, roleArn));

    const response = await retryOver30Seconds(() => getFunction(funcName));
    expect(path(["Configuration", "FunctionName"], response)).toBe(funcName);
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
      expect(prop("name", err)).toBe("ResourceNotFoundException");
    }
  };

  const testListFunctions = async () => {
    const getFunctionNames = compose(
      andThen(map(prop("FunctionName"))),
      andThen(prop("Functions")),
      listFunctions
    );

    const functionNames = await getFunctionNames();
    expect(functionNames).toContainEqual(funcName);
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
    await updateFunctionConfiguration(funcName);
    await retryOver30Seconds(testInvokeFunction);
    await testListFunctions();
    await testUpdateFunction();
    await testDeleteFunction();
  });
});
