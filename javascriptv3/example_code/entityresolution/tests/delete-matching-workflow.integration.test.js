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
        workflowName: `${data.inputs.workflowName}`,
      });
    },
    { timeout: 600000 },
  );
});
