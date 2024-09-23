// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/* eslint-disable -- This file existed pre-eslint configuration. Fix the next time the file is touched. */

import { describe, it, expect } from "vitest";
import { v4 as uuidv4 } from "uuid";
import { getMetricData } from "../libs/cloudwatch-helper.js";

describe("put-metric-data", () => {
  it("should create metric data", async () => {
    const mod = await import("../actions/put-metric-data.js");
    await mod.default;

    const id = `id${uuidv4().replace(/\-/g, "_")}`;

    const { MetricDataResults } = await getMetricData(id);
    expect(MetricDataResults[0].Id).toBe(id);
  });
});
