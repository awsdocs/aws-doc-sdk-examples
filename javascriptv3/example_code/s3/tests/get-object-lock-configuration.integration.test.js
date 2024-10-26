// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, afterAll } from "vitest";
import { main as getObjectLockConfiguration } from "../actions/get-object-lock-configuration.js";
import {
  S3Client,
  CreateBucketCommand,
  PutObjectLockConfigurationCommand,
  PutBucketVersioningCommand,
  MFADeleteStatus,
  BucketVersioningStatus,
} from "@aws-sdk/client-s3";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);

describe("get-object-lock-configuration.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should get the object lock configuration of a bucket", async () => {
    // Setup
    await client.send(new CreateBucketCommand({ Bucket: bucketName }));
    await client.send(
      new PutBucketVersioningCommand({
        Bucket: bucketName,
        VersioningConfiguration: {
          MFADelete: MFADeleteStatus.Disabled,
          Status: BucketVersioningStatus.Enabled,
        },
      }),
    );
    await client.send(
      new PutObjectLockConfigurationCommand({
        Bucket: bucketName,
        ObjectLockConfiguration: { ObjectLockEnabled: "Enabled" },
      }),
    );

    // Execute
    const spy = vi.spyOn(console, "error");
    await getObjectLockConfiguration({ bucketName });
    expect(spy).not.toHaveBeenCalled();
  });
});
