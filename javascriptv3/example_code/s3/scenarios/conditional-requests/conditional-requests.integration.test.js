// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { describe, it, expect, afterAll } from "vitest";
import { S3Client, ListBucketsCommand } from "@aws-sdk/client-s3";
import { createBucketsAction } from "./setup.steps.js";
import * as Scenarios from "@aws-doc-sdk-examples/lib/scenario/index.js";
import { legallyEmptyAndDeleteBuckets } from "../../libs/s3Utils.js";

const bucketPrefix = "js-conditional-requests";
const client = new S3Client({});

describe("S3 Object Locking Integration Tests", () => {
  const state = {
    sourceBucketName: `${bucketPrefix}-no-lock`,
    destinationBucketName: `${bucketPrefix}-lock-enabled`,
  };

  afterAll(async () => {
    // Clean up resources
    const buckets = [state.sourceBucketName, state.destinationBucketName];

    await legallyEmptyAndDeleteBuckets(buckets);
  });

  it("should create buckets with correct configurations", async () => {
    const action = createBucketsAction(Scenarios, client);
    await action.handle(state);

    const bucketList = await client.send(new ListBucketsCommand({}));
    expect(bucketList.Buckets?.map((bucket) => bucket.Name)).toContain(
      state.sourceBucketName,
    );
    expect(bucketList.Buckets?.map((bucket) => bucket.Name)).toContain(
      state.destinationBucketName,
    );
  });
});
