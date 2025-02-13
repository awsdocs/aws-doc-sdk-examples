// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/get-object-conditional-request-if-match.js";

describe("test get-object-conditional-request-if-match", () => {
  it(
    "should run without error",
    async () => {
      await main({
        bucketName: "mybucket",
        key: "myKey",
        eTag: "123456789",
      });
    },
    { timeout: 600000 },
  );
});
