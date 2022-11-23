/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, beforeAll, afterAll } from "vitest";
import { createRole, deleteRole } from "../libs/iam-helper.js";

describe("put-subscription-filter", () => {
  const roleName = "put-subscription-filter-test-execution-role";
  let roleArn;
  
  beforeAll(async () => {
    roleArn = await createRole(roleName);
    // create destination
    // create log group
  });

  afterAll(async () => {
    // delete log group
    // delete roles
    await deleteRole(roleName);
    // delete destination
  });

  it("should create a subscription filter", () => {});
});
