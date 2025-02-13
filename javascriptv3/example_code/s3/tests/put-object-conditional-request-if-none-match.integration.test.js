// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/put-object-conditional-request-if-none-match.js";

describe("test put-object-conditional-request-if-none-match", () => {
  it(
    "should run without error",
    async () => {
      await main({
        destinationBucketName: "mybucket",
      });
    },
    { timeout: 600000 },
  );
});
