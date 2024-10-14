// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, vi, it, expect, afterAll } from "vitest";
import {
  S3Client,
  PutObjectCommand,
  CreateBucketCommand,
  PutObjectRetentionCommand,
} from "@aws-sdk/client-s3";

import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { main as getObjectRetention } from "../actions/get-object-retention.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);
const objectKey = "test-object";

describe("get-object-retention.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should get the object retention settings of an object", async () => {
    const retainUntilDate = new Date(
      new Date().getTime() + 24 * 60 * 60 * 1000,
    );
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
          RetainUntilDate: retainUntilDate,
        },
      }),
    );

    const spy = vi.spyOn(console, "log");
    await getObjectRetention({ bucketName, key: objectKey });
    expect(spy).toHaveBeenCalledWith(
      `${objectKey} in ${bucketName} will be retained until ${retainUntilDate}`,
    );
  });
});
