// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { vi, describe, it, expect, afterAll } from "vitest";
import {
  S3Client,
  CreateBucketCommand,
  PutObjectLegalHoldCommand,
  PutObjectCommand,
} from "@aws-sdk/client-s3";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { main as getObjectLegalHold } from "../actions/get-object-legal-hold.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);
const objectKey = "test-object";

describe("get-object-legal-hold.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should get object legal hold", async () => {
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
      new PutObjectLegalHoldCommand({
        Bucket: bucketName,
        Key: objectKey,
        LegalHold: { Status: "ON" },
      }),
    );

    const spy = vi.spyOn(console, "log");
    await getObjectLegalHold({ bucketName, key: objectKey });
    expect(spy).toHaveBeenCalledWith("Legal Hold Status: ON");
  });
});
