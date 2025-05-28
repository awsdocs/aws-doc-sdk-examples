// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/create-map.js";
import data from "../actions/inputs.json";

describe("test create-map", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        MapName: `${data.inputs.mapName}`,
        Configuration: { style: "VectorEsriNavigation" },
      });
    },
    { timeout: 600000 },
  );
});
