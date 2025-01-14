// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { S3ServiceException } from "@aws-sdk/client-s3";
import { describe, it, expect, vi } from "vitest";

const paginateListBuckets = vi.fn().mockImplementation(async function* () {
  yield {
    Buckets: [{ Name: "amzn-s3-demo-bucket" }],
    Owner: { DisplayName: "bar" },
  };
});

vi.doMock("@aws-sdk/client-s3", async () => {
  const actual = await vi.importActual("@aws-sdk/client-s3");
  return {
    ...actual,
    paginateListBuckets,
  };
});

const { main } = await import("../actions/list-buckets.js");

describe("list-buckets", () => {
  it("should log the response from the service", async () => {
    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenNthCalledWith(1, "bar owns 1 bucket:");
    expect(spy).toHaveBeenNthCalledWith(2, " • amzn-s3-demo-bucket");
  });

  it("should indicate a failure came from S3 when the error isn't generic", async () => {
    const error = new S3ServiceException("Some S3 service exception.");
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.name = "ServiceException";
    const bucketName = "amzn-s3-demo-bucket";
    paginateListBuckets.mockImplementationOnce(
      // biome-ignore  lint/correctness/useYield: Mock generator
      async function* () {
        throw error;
      },
    );

    const spy = vi.spyOn(console, "error");

    await main({ bucketName, keys: ["foo"] });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while listing buckets.  ${error.name}: ${error.message}`,
    );
  });

  it("should throw errors that are not S3 specific", async () => {
    const bucketName = "amzn-s3-demo-bucket";
    paginateListBuckets.mockImplementationOnce(
      // biome-ignore  lint/correctness/useYield: Mock generator
      async function* () {
        throw new Error();
      },
    );

    await expect(() =>
      main({ bucketName, keys: ["foo"] }),
    ).rejects.toBeTruthy();
  });
});
