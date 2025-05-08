// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/list-schema-mappings.js";
import data from "../inputs.json";

describe("test list-schema-mappings", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        workflowName: `${data.inputs.workflowName}`,
        jobId: `${data.inputs.jobId}`,
      });
    },
    { timeout: 600000 },
  );
});
