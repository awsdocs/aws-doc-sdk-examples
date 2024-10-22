// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, vi, expect, afterAll } from "vitest";
import {
  S3Client,
  CreateBucketCommand,
  PutObjectCommand,
  GetObjectLegalHoldCommand,
} from "@aws-sdk/client-s3";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { main as putObjectLegalHold } from "../actions/put-object-legal-hold.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);
const objectKey = "file.txt";

describe("put-object-legal-hold.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should set the legal hold status of an object", async () => {
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
        Body: "content",
      }),
    );

    // Execute
    const spy = vi.spyOn(console, "error");
    await putObjectLegalHold({ bucketName, objectKey, legalHoldStatus: "ON" });
    expect(spy).not.toHaveBeenCalled();

    // Verify
    const { LegalHold } = await client.send(
      new GetObjectLegalHoldCommand({ Bucket: bucketName, Key: objectKey }),
    );
    expect(LegalHold.Status).toBe("ON");
  });
});
