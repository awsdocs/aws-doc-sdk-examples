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

const { main } = await import(
  "../actions/get-object-conditional-request-if-none-match.js"
);

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

    await main({
      bucketName: "amzn-s3-demo-bucket",
      key: "foo",
      eTag: "123456789",
    });

    expect(spy).toHaveBeenCalledWith(
      "Success. Here is text of the file:",
      "foo",
    );
  });

  it("should log a relevant error message when the object key doesn't exist in the bucket", async () => {
    const bucketName = "amzn-s3-demo-bucket";
    const key = "foo";
    const eTag = "123456789";
    const error = new NoSuchKey();
    send.mockRejectedValueOnce(error);

    const spy = vi.spyOn(console, "error");

    await main({ bucketName, key, eTag });

    expect(spy).toHaveBeenCalledWith(
      `Error from S3 while getting object "${key}" from "${bucketName}". No such key exists.`,
    );
  });
});
