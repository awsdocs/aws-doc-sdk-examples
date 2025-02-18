// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/get-object-conditional-request-if-none-match.js";

describe("test get-object-conditional-request-if-none-match", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        bucketName: "amzn-s3-demo-bucket",
        key: "myKey",
        eTag: "123456789",
      });
    },
    { timeout: 600000 },
  );
});
