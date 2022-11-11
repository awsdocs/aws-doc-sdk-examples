/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import { makeGetDatabaseStep } from "../scenarios/basic/steps/get-database.js";

describe("get-database", () => {
  it("should call getDatabase with the database environment variable", async () => {
    const getDatabase = vi.fn(async () => ({ Database: {} }));
    const actions = { getDatabase };

    process.env.DATABASE_NAME = "db_name";

    const step = makeGetDatabaseStep(actions);
    await step({});
    expect(getDatabase).toHaveBeenCalledWith("db_name");
  });

  it("should return a context object", async () => {
    const getDatabase = vi.fn(async () => ({ Database: {} }));
    const actions = { getDatabase };

    const step = makeGetDatabaseStep(actions);
    const actual = await step({});
    expect(actual).toEqual({});
  });
});
