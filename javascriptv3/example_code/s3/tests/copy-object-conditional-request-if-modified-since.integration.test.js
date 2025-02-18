// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/copy-object-conditional-request-if-modified-since.js";

describe("test copy-object-conditional-request-if-modified-since", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        sourceBucketName: "amzn-s3-demo-bucket",
        sourceKeyName: "mykey",
        destinationBucketName: "amzn-s3-demo-bucket1",
      });
    },
    { timeout: 600000 },
  );
});
