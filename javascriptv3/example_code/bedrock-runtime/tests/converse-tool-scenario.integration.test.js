// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../scenarios/converse_tool_scenario/converse-tool-scenario.js";

describe("basic scenario", () => {
  it(
    "should run without error",
    async () => {
      await main({ confirmAll: true });
    },
    { timeout: 600000 },
  );
});
