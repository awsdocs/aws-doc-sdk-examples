// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/copy-object-conditional-request-if-modified-since.js";

describe("test copy-object-conditional-request-if-modified-since", () => {
  it(
    "should run without error",
    async () => {
      await main({
        sourceBucketName: "mybucket",
        sourceKeyName: "mykey",
        destinationBucketName: "mydestinationbucket",
      });
    },
    { timeout: 600000 },
  );
});
