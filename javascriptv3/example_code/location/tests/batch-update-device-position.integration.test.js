// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/batch-update-device-position.js";
import data from "../actions/inputs.json";

describe("test batch-update-device-position", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        TrackerName: `${data.inputs.trackerName}`,
        Updates: [
          {
            DeviceId: `${data.inputs.deviceId}`,
            SampleTime: new Date(),
            Position: [-122.4194, 37.7749],
          },
        ],
      });
    },
    { timeout: 600000 },
  );
});
