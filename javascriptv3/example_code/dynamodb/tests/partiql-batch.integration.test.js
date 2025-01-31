// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it } from "vitest";

import { main } from "../scenarios/partiql-batch.js";

describe("partiql batch", () => {
  it("should run without error", async () => {
    await main(true);
  });
});
