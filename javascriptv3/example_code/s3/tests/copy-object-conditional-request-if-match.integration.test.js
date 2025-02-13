// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/copy-object-conditional-request-if-match.js";

describe("test copy-object-conditional-request-if-match", () => {
  it(
    "should run without error",
    async () => {
      await main({
        sourceBucketName: "mybucket",
        sourceKeyName: "mykey",
        destinationBucketName: "mydestinationbucket",
        eTag: "123456789",
      });
    },
    { timeout: 600000 },
  );
});
