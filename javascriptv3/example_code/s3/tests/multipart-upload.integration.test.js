// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, beforeAll, afterAll } from "vitest";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { createBucket, deleteBucket, emptyBucket } from "../libs/s3Utils.js";
import { main } from "../scenarios/multipart-upload.js";

describe("multipart-upload", () => {
  const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);

  beforeAll(async () => {
    await createBucket(bucketName);
  });

  afterAll(async () => {
    await emptyBucket(bucketName);
    await deleteBucket(bucketName);
  });

  it("should run", async () => {
    await main({ bucketName, key: "big-string.txt" });
  });
});
