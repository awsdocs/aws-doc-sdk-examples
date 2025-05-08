// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/delete-matching-workflow.js";
import data from "../inputs.json";

describe("test delete-matching-workflow", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        schemaName: `${data.inputs.schemaNameJson}`,
        mappedInputFields: [
          {
            fieldName: "id",
            type: "UNIQUE_ID",
          },
          {
            fieldName: "name",
            type: "NAME",
          },
          {
            fieldName: "email",
            type: "EMAIL_ADDRESS",
          },
        ],
      });
    },
    { timeout: 600000 },
  );
});
