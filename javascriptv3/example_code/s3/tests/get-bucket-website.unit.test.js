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

const { main } = await import("../actions/get-bucket-website.js");

describe("get-bucket-website", () => {
  it("should log the response from the service", async () => {
    const mockResponse = {
      IndexDocument: { Suffix: "foo" },
      ErrorDocument: { Key: "bar" },
    };
    send.mockResolvedValue(mockResponse);
    const bucketName = "amzn-s3-demo-bucket";

    const spy = vi.spyOn(console, "log");

    await main({ bucketName });

    expect(spy).toHaveBeenCalledWith(
      `Your bucket is set up to host a website with the following configuration:\n${JSON.stringify(mockResponse, null, 2)}`,
    );
  });

  it("should log a relevant error when the bucket isn't configured as a website.", async () => {
    const error = new S3ServiceException("Not such website configuration.");
    error.name = "NoSuchWebsiteConfiguration";
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while getting website configuration for ${bucketName}. The bucket isn't configured as a website.`,
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
      `Error from S3 while getting website configuration for ${bucketName}.  ${error.name}: ${error.message}`,
    );
  });

  it("should throw errors that are not S3 specific", async () => {
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(new Error());

    await expect(() => main({ bucketName })).rejects.toBeTruthy();
  });
});
