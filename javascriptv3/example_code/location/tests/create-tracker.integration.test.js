// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/create-tracker.js";
import data from "../actions/inputs.json";

describe("test create-tracker", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        TrackerName: `${data.inputs.trackerName}`,
      });
    },
    { timeout: 600000 },
  );
});
