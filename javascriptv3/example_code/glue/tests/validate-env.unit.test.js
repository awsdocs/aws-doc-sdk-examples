/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect } from "vitest";
import { validateEnv } from "../scenarios/basic/steps/validate-env.js";

describe("validateEnv", () => {
  it("should throw an error if no arguments are passed", () => {
    expect(validateEnv()).rejects.toBeTruthy();
  });

  it("should throw an error if no envVars are passed in the context", () => {
    expect(validateEnv({})).rejects.toBeTruthy();
  });

  it("should throw an error if empty envVars are passed in the context", () => {
    expect(
      validateEnv({ envVars: { VAR_1: "", VAR_2: "Hello" } })
    ).rejects.toBeTruthy();
  });

  it("should return a copy of the context if all envVars are present", () => {
    const context = { envVars: { VAR_1: "var_1", VAR_2: "var_2" } };
    expect(validateEnv(context)).resolves.toEqual(context);
    expect(validateEnv(context)).resolves.not.toBe(context);
  });
});
