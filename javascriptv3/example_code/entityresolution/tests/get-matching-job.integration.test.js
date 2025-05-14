// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/get-matching-job.js";
import data from "../inputs.json";

describe("test get-matching-job", () => {
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
