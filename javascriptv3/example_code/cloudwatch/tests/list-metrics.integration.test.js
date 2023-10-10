/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect } from "vitest";
import { main as listMetrics } from "../actions/list-metrics.js";

describe("list-metrics", () => {
  it("should list metrics", async () => {
    const { Metrics } = await listMetrics();
    expect(Metrics.length).toBeGreaterThan(0);
  });
});
