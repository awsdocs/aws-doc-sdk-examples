// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it } from "vitest";
import { main } from "../scenarios/basic.js";

describe("basic scenario", () => {
  it("should run without error", async () => {
    await main(true);
  });
});
