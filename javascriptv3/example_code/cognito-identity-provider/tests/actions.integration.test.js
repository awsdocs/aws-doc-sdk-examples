/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, afterAll } from "vitest";
import { createUserPool } from "../actions/create-user-pool.js";
import { getUniqueName } from "@aws-sdk-examples/libs/utils/util-string.js";
import { deleteUserPool } from "../actions/delete-user-pool.js";
import { createUserPoolClient } from "../actions/create-user-pool-client.js";
import { listUsers } from "../actions/list-users.js";
import { helloCognito } from "../hello.js";

const testCreateUserPool = async (poolName) => {
  const {
    $metadata: { httpStatusCode },
    UserPool: { Id },
  } = await createUserPool(poolName);

  expect(httpStatusCode).toBe(200);

  return Id;
};

const testHello = async (userPoolName) => {
  const userPoolNames = await helloCognito();
  expect(userPoolNames).toContain(userPoolName);
};

const testCreateUserPoolClient = async (clientName, poolId) => {
  const {
    $metadata: { httpStatusCode },
    UserPoolClient: { ClientId },
  } = await createUserPoolClient(clientName, poolId);

  expect(httpStatusCode).toBe(200);
  return ClientId;
};

const testListUsers = async (userPoolId) => {
  const {
    $metadata: { httpStatusCode },
    Users,
  } = await listUsers({ userPoolId });
  expect(httpStatusCode).toBe(200);
  expect(Users).toEqual([]);
  return Users;
};

describe("cognito-identity-provider actions", () => {
  let poolId;

  afterAll(async () => {
    try {
      await deleteUserPool(poolId);
    } catch (err) {
      console.error(err);
    }
  });

  it("should successfully interact with Cognito", async () => {
    expect.assertions(5);
    const userPoolName = getUniqueName("create-user-pool-test");
    poolId = await testCreateUserPool(userPoolName);
    await testHello(userPoolName);

    await testCreateUserPoolClient(
      getUniqueName("create-user-pool-test"),
      poolId,
    );

    await testListUsers(poolId);
  });
});
