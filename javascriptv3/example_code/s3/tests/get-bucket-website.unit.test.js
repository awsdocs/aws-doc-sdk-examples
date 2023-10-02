/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
    send.mockResolvedValue({
      IndexDocument: { Suffix: "foo" },
      ErrorDocument: { Key: "bar" },
    });

    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenCalledWith(
      "Your bucket is set up to host a website. It has an error document:",
      "bar, and an index document: foo.",
    );
  });

  it("should log errors", async () => {
    send.mockRejectedValue("foo");

    const spy = vi.spyOn(console, "error");

    await main();

    expect(spy).toHaveBeenCalledWith("foo");
  });
});
