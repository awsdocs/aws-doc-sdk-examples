// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
  "../actions/copy-object-conditional-request-if-none-match.js"
);

describe("copy-object", () => {
  const sourceBucket = "amzn-s3-demo-bucket";
  const sourceKey = "todo.txt";
  const destinationBucket = "amzn-s3-demo-bucket1";
  const destinationKey = "updated-todo.txt";

  it("should log the response from the service", async () => {
    send.mockResolvedValue("foo");

    const spy = vi.spyOn(console, "log");

    await main({ sourceBucket, sourceKey, destinationBucket, destinationKey });

    expect(spy).toHaveBeenCalledWith("Successfully copied object to bucket.");
  });
});
