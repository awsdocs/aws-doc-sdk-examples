/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, expect } from "@jest/globals";
import { createUserPool } from "../actions/create-user-pool.js";
import { getUniqueName } from "../../libs/utils/util-string.js";
import { deleteUserPool } from "../actions/delete-user-pool.js";
import { createUserPoolClient } from "../actions/create-user-pool-client.js";
import { listUsers } from "../actions/list-users.js";

const testCreateUserPool = async (poolName) => {
  const {
    $metadata: { httpStatusCode },
    UserPool: { Id },
  } = await createUserPool(poolName);

  expect(httpStatusCode).toBe(200);

  return Id;
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
    expect.assertions(4);
    poolId = await testCreateUserPool(getUniqueName("create-user-pool-test"));

    await testCreateUserPoolClient(
      getUniqueName("create-user-pool-test"),
      poolId
    );

    await testListUsers(poolId);
  });
});
