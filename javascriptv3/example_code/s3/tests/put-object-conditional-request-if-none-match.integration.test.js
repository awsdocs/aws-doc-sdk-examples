// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { main } from "../actions/put-object-conditional-request-if-none-match.js";

describe("test put-object-conditional-request-if-none-match", () => {
  it(
    "should not re-throw service exceptions",
    async () => {
      await main({
        destinationBucketName: "amzn-s3-demo-bucket1",
      });
    },
    { timeout: 600000 },
  );
});
