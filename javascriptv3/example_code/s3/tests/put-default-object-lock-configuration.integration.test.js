// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, afterAll } from "vitest";
import { main as putDefaultObjectLockConfiguration } from "../actions/put-default-object-lock-configuration.js";
import {
  S3Client,
  CreateBucketCommand,
  GetObjectLockConfigurationCommand,
  PutBucketVersioningCommand,
  MFADeleteStatus,
  BucketVersioningStatus,
} from "@aws-sdk/client-s3";
import { getUniqueName } from "@aws-doc-sdk-examples/lib/utils/util-string.js";
import { legallyEmptyAndDeleteBuckets } from "../libs/s3Utils.js";

const client = new S3Client({});
const bucketName = getUniqueName(process.env.S3_BUCKET_NAME_PREFIX);

describe("put-default-object-lock-configuration.js Integration Test", () => {
  afterAll(async () => {
    // Clean up test resources
    await legallyEmptyAndDeleteBuckets([bucketName]);
  });

  it("should set the default object lock configuration on a bucket", async () => {
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

    // Execute
    const spy = vi.spyOn(console, "error");
    await putDefaultObjectLockConfiguration({ bucketName, retentionDays: 1 });
    expect(spy).not.toHaveBeenCalled();

    // Verify
    const { ObjectLockConfiguration } = await client.send(
      new GetObjectLockConfigurationCommand({ Bucket: bucketName }),
    );
    expect(ObjectLockConfiguration.ObjectLockEnabled).toBe("Enabled");
    expect(ObjectLockConfiguration.Rule.DefaultRetention.Mode).toBe(
      "GOVERNANCE",
    );
    expect(ObjectLockConfiguration.Rule.DefaultRetention.Days).toBe(1);
  });
});
