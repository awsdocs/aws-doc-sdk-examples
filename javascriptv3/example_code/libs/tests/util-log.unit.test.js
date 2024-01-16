// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { it, describe, expect } from "vitest";
import { log } from "../utils/util-log";

describe("log", () => {
  it("should parse a string from an object", () => {
    expect(log({})).toEqual("{}");
  });
});
