/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { it, describe, vi, expect } from "vitest";
import { makeCleanUpTablesStep } from "../scenarios/basic/steps/clean-up-tables.js";
import { mockPrompter } from "./mock-prompter.js";

describe("clean-up-tables", () => {
  it("should not prompt to delete, or delete, any tables if none exist", async () => {
    const getTables = vi.fn(async () => ({ TableList: [] }));
    const deleteTable = vi.fn(async () => {});
    const prompter = mockPrompter();
    const cleanUpTablesStep = makeCleanUpTablesStep({
      getTables,
      deleteTable,
    });

    await cleanUpTablesStep({ prompter, envVars: { DATABASE_NAME: "db1" } });
    expect(deleteTable).not.toHaveBeenCalled();
    expect(prompter.prompt).not.toHaveBeenCalled();
  });

  it("should prompt to delete, and delete, if tables exist and are selected", async () => {
    const getTables = vi.fn(async () => ({ TableList: ["tb1"] }));
    const deleteTable = vi.fn(async () => {});
    const prompter = mockPrompter({ tableNames: ["tb1"] });
    const cleanUpTablesStep = makeCleanUpTablesStep({
      getTables,
      deleteTable,
    });

    await cleanUpTablesStep({ prompter, envVars: { DATABASE_NAME: "db1" } });
    expect(deleteTable).toHaveBeenCalled("tb1");
    expect(prompter.prompt).toHaveBeenCalled();
  });

  it("should prompt to delete, but not delete, if tables exist but are not selected", async () => {
    const getTables = vi.fn(async () => ({ TableList: ["tb1"] }));
    const deleteTable = vi.fn(async () => {});
    const prompter = mockPrompter({ tableNames: [] });
    const cleanUpTablesStep = makeCleanUpTablesStep({
      getTables,
      deleteTable,
    });

    await cleanUpTablesStep({ prompter, envVars: { DATABASE_NAME: "db1" } });
    expect(deleteTable).not.toHaveBeenCalled();
    expect(prompter.prompt).toHaveBeenCalled();
  });

  it("should return a context object", async () => {
    const getTables = vi.fn(async () => ({}));
    const deleteTable = vi.fn(async () => {});
    const actions = { getTables, deleteTable };

    const context = { envVars: {} };

    const step = makeCleanUpTablesStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
