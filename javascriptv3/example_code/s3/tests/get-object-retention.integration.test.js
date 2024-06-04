// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, vi, it, expect, afterAll } from "vitest";
import {
  S3Client,
  PutObjectCommand,
  CreateBucketCommand,
  PutObjectRetentionCommand,
  GetObjectRetentionCommand,
} from "@aws-sdk/client-s3";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { main as getObjectRetention } from "../actions/get-object-retention.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName("test-bucket");
const objectKey = "test-object";

describe("get-object-retention.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should get the object retention settings of an object", async () => {
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
    await client.send(
      new PutObjectRetentionCommand({
        Bucket: bucketName,
        Key: objectKey,
        Retention: {
          Mode: "GOVERNANCE",
          RetainUntilDate: new Date(new Date().getTime() + 24 * 60 * 60 * 1000),
        },
      }),
    );

    // Execute
    const spy = vi.spyOn(console, "error");
    await getObjectRetention(client, bucketName, objectKey);
    expect(spy).not.toHaveBeenCalled();

    // Verify
    const { Retention } = await client.send(
      new GetObjectRetentionCommand({ Bucket: bucketName, Key: objectKey }),
    );
    expect(Retention.Mode).toBe("GOVERNANCE");
    expect(Retention.RetainUntilDate).toBeDefined();
  });
});
