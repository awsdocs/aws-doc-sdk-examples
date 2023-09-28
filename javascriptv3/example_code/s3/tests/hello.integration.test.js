/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { S3Client } from "@aws-sdk/client-s3";
import { describe, it, expect, beforeAll, afterAll } from "vitest";
import { CreateBucketCommand, DeleteBucketCommand } from "@aws-sdk/client-s3";
import { helloS3 } from "../hello.js";

describe("helloS3", () => {
  const bucketName = `hello-s3-bucket-${Math.ceil(Math.random() * 1000000)}`;
  const s3Client = new S3Client({});

  beforeAll(async () => {
    await s3Client.send(new CreateBucketCommand({ Bucket: bucketName }));
  });

  afterAll(async () => {
    await s3Client.send(new DeleteBucketCommand({ Bucket: bucketName }));
  });

  it("should return a list of bucket names", async () => {
    const buckets = await helloS3();
    expect(buckets.map((b) => b.Name)).toContain(bucketName);
  });
});
