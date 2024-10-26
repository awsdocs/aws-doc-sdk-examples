// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect } from "vitest";
import { main } from "../scenarios/basic.js";

describe("basic", () => {
  it("should run without error", async () => {
    await expect(() => main({ confirmAll: true })).rejects.not.toThrowError();
  });
});
