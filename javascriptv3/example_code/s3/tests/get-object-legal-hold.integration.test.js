// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { vi, describe, it, expect, afterAll } from "vitest";
import {
  S3Client,
  CreateBucketCommand,
  PutObjectLegalHoldCommand,
  PutObjectCommand,
  GetObjectLegalHoldCommand,
} from "@aws-sdk/client-s3";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { main as getObjectLegalHold } from "../actions/get-object-legal-hold.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName("test");
const objectKey = "test-object";

describe("get-object-legal-hold.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should get object legal hold", async () => {
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
      new PutObjectLegalHoldCommand({
        Bucket: bucketName,
        Key: objectKey,
        LegalHold: { Status: "ON" },
      }),
    );

    // Execute
    const spy = vi.spyOn(console, "error");
    await getObjectLegalHold(client, bucketName, objectKey);
    expect(spy).not.toHaveBeenCalled();

    // Verify
    const { LegalHold } = await client.send(
      new GetObjectLegalHoldCommand({ Bucket: bucketName, Key: objectKey }),
    );
    expect(LegalHold.Status).toBe("ON");
  });
});
