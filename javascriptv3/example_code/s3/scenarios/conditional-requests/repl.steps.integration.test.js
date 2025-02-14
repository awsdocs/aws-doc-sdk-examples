// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it } from "vitest";
import { replAction } from "./repl.steps.js";
import { S3Client } from "@aws-sdk/client-s3";

describe("basic scenario", () => {
  it(
    "should run without error",
    async () => {
      await replAction({ confirmAll: true }, S3Client);
    },
    { timeout: 600000 },
  );
});
