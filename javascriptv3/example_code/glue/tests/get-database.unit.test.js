/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import * as envVars from "../scenarios/basic/env.js";
import { makeGetDatabaseStep } from "../scenarios/basic/steps/get-database.js";

describe("get-database", () => {
  it('should call getDatabase with the database environment variable', async () => {
    const getDatabase = vi.fn(async () => ({ Database: {} }));
    const actions = { getDatabase };

    const context = { envVars };

    const step = makeGetDatabaseStep(actions);
    await step(context);
    expect(getDatabase).toHaveBeenCalledWith(envVars.DATABASE_NAME)
  });

  it("should return a context object", async () => {
    const getDatabase = vi.fn(async () => ({ Database: {} }));
    const actions = { getDatabase };

    const context = { envVars };

    const step = makeGetDatabaseStep(actions);
    const actual = await step(context);
    expect(actual).toEqual(context);
  });
});
