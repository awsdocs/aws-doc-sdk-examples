// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../scenarios/entity-resolution-basics.js";

describe("Entity Resolution basic scenario", () => {
  it(
    "should run without error",
    async () => {
      await main({ confirmAll: true });
    },
    { timeout: 600000 },
  );
});
