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

const { main } = await import("../actions/list-objects.js");

describe("list-objects", () => {
  it("should log the response from the service", async () => {
    send.mockResolvedValue({ Contents: [{ Key: "foo" }] });

    const spy = vi.spyOn(console, "log");

    await main();

    expect(spy).toHaveBeenNthCalledWith(
      1,
      "Your bucket contains the following objects:\n",
    );
    expect(spy).toHaveBeenNthCalledWith(2, " • foo\n");
  });

  it("should log errors", async () => {
    send.mockRejectedValue("foo");

    const spy = vi.spyOn(console, "error");

    await main();

    expect(spy).toHaveBeenCalledWith("foo");
  });
});
