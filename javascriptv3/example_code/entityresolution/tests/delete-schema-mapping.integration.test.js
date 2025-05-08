// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/delete-schema-mapping.js";
import data from "../inputs.json";

describe("test delete-schema-mapping", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        schemaName: `${data.inputs.schemaNameJson}`,
      });
    },
    { timeout: 600000 },
  );
});
