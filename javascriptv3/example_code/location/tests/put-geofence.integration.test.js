// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/put-geofence.js";
import data from "../actions/inputs.json";

describe("test put-geofence", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        CollectionName: `${data.inputs.collectionName}`,
        GeofenceId: `${data.inputs.geoId}`,
        Geometry: {
          Polygon: [
            [
              [-122.3381, 47.6101],
              [-122.3281, 47.6101],
              [-122.3281, 47.6201],
              [-122.3381, 47.6201],
              [-122.3381, 47.6101],
            ],
          ],
        },
      });
    },
    { timeout: 600000 },
  );
});
