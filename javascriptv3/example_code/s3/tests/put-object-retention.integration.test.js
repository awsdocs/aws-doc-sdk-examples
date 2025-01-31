// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, afterAll, vi } from "vitest";
import {
  S3Client,
  CreateBucketCommand,
  PutObjectCommand,
  GetObjectRetentionCommand,
} from "@aws-sdk/client-s3";
import { main as putObjectRetention } from "../actions/put-object-retention.js";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);
const objectKey = "test-object";

describe("put-object-retention.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should set the retention settings of an object", async () => {
    // Setup
    await client.send(
      new CreateBucketCommand({
        Bucket: bucketName,
        ObjectLockEnabledForBucket: true,
      }),
    );
    await client.send(
      new PutObjectCommand({
        Bucket: bucketName,
        Key: objectKey,
        Body: "test content",
      }),
    );

    // Execute
    const spy = vi.spyOn(console, "error");
    await putObjectRetention({ bucketName, key: objectKey });
    expect(spy).not.toHaveBeenCalled();

    // Verify
    const { Retention } = await client.send(
      new GetObjectRetentionCommand({ Bucket: bucketName, Key: objectKey }),
    );

    expect(Retention.Mode).toBe("GOVERNANCE");
    expect(Retention.RetainUntilDate).toBeDefined();
  });
});
