/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

import { makeGetTablesStep } from "../scenarios/basic/steps/get-tables.js";

describe("get-tables", () => {
  const getTables = vi.fn(async () => ({ TableList: [] }));
  const actions = { getTables };
  const step = makeGetTablesStep(actions);

  it("should call getTables with the database name from the environment variables", async () => {
    process.env.DATABASE_NAME = "db_name";
    await step({});
    expect(getTables).toHaveBeenCalledWith("db_name");
  });

  it("should return a context object", async () => {
    const actual = await step({});
    expect(actual).toEqual({});
  });
});
