/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

import * as envVars from "../scenarios/basic/env.js";
import { makeGetTablesStep } from "../scenarios/basic/steps/get-tables.js";

describe("get-tables", () => {
  const getTables = vi.fn(async () => ({ TableList: [] }));
  const actions = { getTables };
  const step = makeGetTablesStep(actions);
  const context = { envVars };
  
  it("should call getTables with the database name from the environment variables", async () => {
    await step(context);
    expect(getTables).toHaveBeenCalledWith(envVars.DATABASE_NAME);
  });

  it("should return a context object", async () => {
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
