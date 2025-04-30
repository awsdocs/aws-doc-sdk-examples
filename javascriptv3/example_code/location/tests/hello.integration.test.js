// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../hello.js";

describe("test hello", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main();
    },
    { timeout: 600000 },
  );
});
