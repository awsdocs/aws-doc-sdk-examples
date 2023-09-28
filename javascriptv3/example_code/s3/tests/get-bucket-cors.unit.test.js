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

const { main } = await import("../actions/get-bucket-cors.js");

describe("get-bucket-cors", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue({
      CORSRules: [
        {
          AllowedHeaders: ["foo"],
          AllowedMethods: ["bar"],
          AllowedOrigins: ["baz"],
          ExposeHeaders: ["qux"],
          MaxAgeSeconds: 123,
        },
      ],
    });

    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenCalledWith(
      `\nCORSRule 1`,
      `\n${"-".repeat(10)}`,
      `\nAllowedHeaders: foo`,
      `\nAllowedMethods: bar`,
      `\nAllowedOrigins: baz`,
      `\nExposeHeaders: qux`,
      `\nMaxAgeSeconds: 123`,
    );
  });

  it("should log errors", async () => {
    send.mockRejectedValue("foo");

    const spy = vi.spyOn(console, "error");

    await main();

    expect(spy).toHaveBeenCalledWith("foo");
  });
});
