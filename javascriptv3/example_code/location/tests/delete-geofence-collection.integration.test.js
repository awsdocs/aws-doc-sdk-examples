// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/delete-geofence-collection.js";
import data from "../actions/inputs.json";

describe("test delete-geofence-collection", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        CollectionName: `${data.inputs.collectionName}`,
      });
    },
    { timeout: 600000 },
  );
});
