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

import { main } from "../actions/list-buckets.js";

describe("list-buckets", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue({
      Buckets: [{ Name: "foo" }],
      Owner: { DisplayName: "bar" },
    });

    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenNthCalledWith(1, "bar owns 1 bucket:");
    expect(spy).toHaveBeenNthCalledWith(2, " • foo");
  });

  it("should log errors", async () => {
    send.mockRejectedValue("foo");

    const spy = vi.spyOn(console, "error");

    await main();

    expect(spy).toHaveBeenCalledWith("foo");
  });
});
