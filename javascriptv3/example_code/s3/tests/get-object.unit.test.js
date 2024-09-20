// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { NoSuchKey, S3ServiceException } from "@aws-sdk/client-s3";
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

const { main } = await import("../actions/get-object.js");

describe("get-object", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue({
      Body: {
        transformToString() {
          return Promise.resolve("foo");
        },
      },
    });

    const spy = vi.spyOn(console, "log");

    await main({ bucketName: "my-bucket", key: "foo" });

    expect(spy).toHaveBeenCalledWith("foo");
  });

  it("should log a relevant error message when the object key doesn't exist in the bucket", async () => {
    const bucketName = "my-bucket";
    const key = "foo";
    send.mockRejectedValueOnce(new NoSuchKey());

    const spy = vi.spyOn(console, "error");

    await main({ bucketName, key });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while getting object "${key}" from "${bucketName}". No such key exists.`,
    );
  });

  it("should indicate a failure came from S3 when the error isn't generic", async () => {
    const error = new S3ServiceException("Some S3 service exception.");
    error.name = "ServiceException";
    const bucketName = "my-bucket";
    const key = "foo";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName, key });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while getting object from ${bucketName}.  ${error.name}: ${error.message}`,
    );
  });

  it("should throw errors that are not S3 specific", async () => {
    const bucketName = "my-bucket";
    const key = "foo";
    send.mockRejectedValueOnce(new Error());

    await expect(() => main({ bucketName, key })).rejects.toBeTruthy();
  });
});
