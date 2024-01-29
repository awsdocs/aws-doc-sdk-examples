// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it } from "vitest";

import { scenarios } from "../index.js";

describe("workflow", () => {
  it("should run without error", async () => {
    try {
      await scenarios.deploy.run({ confirmAll: true, verbose: true });
      await scenarios.demo.run({ confirmAll: true, verbose: true });
      await scenarios.destroy.run({ confirmAll: true, verbose: true });
    } catch (err) {
      await scenarios.destroy.run({ confirmAll: true, verbose: true });
      throw err;
    }
  });
});
