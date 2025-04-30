// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/get-device-position.js";
import data from "../actions/inputs.json";
const deviceId = `${data.inputs.deviceId}`;
const trackerName = `${data.inputs.trackerName}`;
describe("test get-device-position", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        DeviceId: deviceId,
        TrackerName: trackerName,
      });
    },
    { timeout: 600000 },
  );
});
