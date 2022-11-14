/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";
import { makeValidatePythonScriptStep } from "../scenarios/basic/steps/validate-python-script.js";

describe("validate-python-script", () => {
  it("should throw an error if the script is not found", async () => {
    const s3ListObjects = vi.fn(async () => ({
      Contents: [{ Key: "WrongKey" }],
    }));
    const actions = { s3ListObjects };

    const step = makeValidatePythonScriptStep(actions);
    return expect(step({})).rejects.toEqual(
      new Error(
        "Missing ETL python script. Did you run the setup steps in the readme?"
      )
    );
  });

  it("should return a context object", async () => {
    process.env.PYTHON_SCRIPT_KEY = "scriptName";
    const s3ListObjects = vi.fn(async () => ({
      Contents: [{ Key: "scriptName" }],
    }));
    const actions = { s3ListObjects };

    const step = makeValidatePythonScriptStep(actions);
    const actual = await step({});
    expect(actual).toEqual({});
  });
});
