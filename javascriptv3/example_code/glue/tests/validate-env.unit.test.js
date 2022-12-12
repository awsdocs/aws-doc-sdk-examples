/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import { describe, it, expect, beforeAll } from "vitest";
import { validateEnv, keys } from "../scenarios/basic/steps/validate-env.js";

describe("validateEnv", () => {
  beforeAll(() => {
    keys.forEach((key) => {
      process.env[key] = "";
    });
  });

  it("should throw an error if no arguments are passed", () => {
    return expect(validateEnv()).rejects.toBeTruthy();
  });

  it.each(keys)(
    "should throw an error if any of the the env vars are missing",
    () => {
      return expect(validateEnv({})).rejects.toBeTruthy();
    }
  );

  it("should return a copy of the context if all env vars are present", async () => {
    const context = {};
    keys.forEach((key) => (process.env[key] = "some value"));
    console.log(process.env);
    await expect(validateEnv(context)).resolves.toEqual(context);
    return expect(validateEnv(context)).resolves.not.toBe(context);
  });
});
