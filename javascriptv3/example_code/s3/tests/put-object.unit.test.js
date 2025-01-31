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

vi.doMock("fs/promises", () => {
  return {
    readFile: () => Promise.resolve(Buffer.from("buffer")),
  };
});

const { main } = await import("../actions/put-object.js");

describe("put-object", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue("foo");

    const spy = vi.spyOn(console, "log");

    await main({
      bucketName: "amzn-s3-demo-bucket",
      key: "movies.json",
      filePath: "path/to/movies.json",
    });

    expect(spy).toHaveBeenCalledWith("foo");
  });

  it("should log a relevant error when the bucket doesn't exist", async () => {
    const error = new S3ServiceException("The specified bucket does not exist");
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.name = "EntityTooLarge";
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({
      bucketName,
      key: "movies.json",
      filePath: "path/to/movies.json",
    });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while uploading object to ${bucketName}. \
The object was too large. To upload objects larger than 5GB, use the S3 console (160GB max) \
or the multipart upload API (5TB max).`,
    );
  });

  it("should indicate a failure came from S3 when the error isn't generic", async () => {
    const error = new S3ServiceException({
      message: "Some S3 service exception.",
    });
    error.$fault = "server"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.$metadata = "metadata"; // Workaround until PR is released. https://github.com/smithy-lang/smithy-typescript/pull/1503
    error.name = "ServiceException";
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({
      bucketName,
      key: "movies.json",
      filePath: "path/to/movies.json",
    });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while uploading object to ${bucketName}.  ${error.name}: ${error.message}`,
    );
  });

  it("should throw errors that are not S3 specific", async () => {
    const bucketName = "amzn-s3-demo-bucket";
    send.mockRejectedValueOnce(new Error());

    await expect(() =>
      main({ bucketName, key: "movies.json", filePath: "path/to/movies.json" }),
    ).rejects.toBeTruthy();
  });
});
