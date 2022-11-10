/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, expect, it, vi } from "vitest";
import { makeCleanUpDatabasesStep } from "../scenarios/basic/steps/clean-up-databases.js";
import { mockPrompter } from "./mock-prompter.js";

describe("clean-up-databases", () => {
  it("should not ask, or attempt, to delete, any databases if none are found", () => {
    const getDatabases = vi.fn(async () => ({ DatabaseList: [] }));
    const deleteDatabase = vi.fn(async (_dbName) => {});
    const prompter = mockPrompter();
    const cleanUpDatabasesStep = makeCleanUpDatabasesStep({
      getDatabases,
      deleteDatabase,
    });

    cleanUpDatabasesStep({ prompter });
    expect(deleteDatabase).not.toHaveBeenCalled();
    expect(prompter.prompt).not.toHaveBeenCalled();
  });

  it("should ask to delete, and delete, databases if any are found", async () => {
    const getDatabases = vi.fn(async () => ({
      DatabaseList: [{ Name: "db1" }],
    }));
    const deleteDatabase = vi.fn(async (_dbName) => {});
    const prompter = mockPrompter({ dbNames: ['db1']});

    const cleanUpDatabasesStep = makeCleanUpDatabasesStep({
      getDatabases,
      deleteDatabase,
    });

    await cleanUpDatabasesStep({ prompter });
    expect(deleteDatabase).toHaveBeenCalled();
    expect(prompter.prompt).toHaveBeenCalled();
  });

  it("should return a context object", async () => {
    const getDatabases = vi.fn(async () => ({ DatabaseList: [] }));
    const deleteDatabase = vi.fn(async () => {});
    const actions = { getDatabases, deleteDatabase };

    const context = { envVars: {} };

    const step = makeCleanUpDatabasesStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
