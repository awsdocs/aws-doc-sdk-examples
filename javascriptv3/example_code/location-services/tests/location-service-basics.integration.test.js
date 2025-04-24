// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../scenarios/location-service-basics.js";

describe("basic scenario", () => {
  it(
    "should run without error",
    async () => {
      await main({ confirmAll: true });
    },
    { timeout: 600000 },
  );
});
