// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { S3ServiceException } from "@aws-sdk/client-s3";
import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-s3", async () => {
  const actual = await vi.importActual("@aws-sdk/client-s3");
  return {
    ...actual,
    S3Client: class {
      send = send;
    },
  };
});

const { main } = await import("../actions/put-bucket-website.js");

describe("put-bucket-website", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue("foo");
    const bucketName = "amzn-s3-demo-bucket";
    const spy = vi.spyOn(console, "log");

    await main({ bucketName });

    expect(spy).toHaveBeenCalledWith(
      `The bucket "${bucketName}" has been configured as a static website.`,
    );
  });

  it("should log a relevant error when the bucket doesn't exist", async () => {
    const error = new S3ServiceException("The specified bucket does not exist");
    error.name = "NoSuchBucket";
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while configuring the bucket "${bucketName}" as a static website. The bucket doesn't exist.`,
    );
  });

  it("should indicate a failure came from S3 when the error isn't generic", async () => {
    const error = new S3ServiceException("Some S3 service exception.");
    error.name = "ServiceException";
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while configuring the bucket "${bucketName}" as a static website. ${error.name}: ${error.message}`,
    );
  });

  it("should throw errors that are not S3 specific", async () => {
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(new Error());

    await expect(() => main({ bucketName })).rejects.toBeTruthy();
  });
});
