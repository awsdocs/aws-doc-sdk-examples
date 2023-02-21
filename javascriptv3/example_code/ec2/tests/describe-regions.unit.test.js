/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-ec2", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ec2");
  return {
    ...actual,
    EC2Client: class {
      send = send;
    },
  };
});

import { main } from "../actions/describe-regions.js";

describe("describe-regions", () => {
  it("should log a list of regions", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({
      Regions: [{ RegionName: "foo" }],
    });

    await main();

    expect(logSpy).toHaveBeenNthCalledWith(1, "Found regions:");
    expect(logSpy).toHaveBeenNthCalledWith(2, " • foo");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to log regions"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(new Error("Failed to log regions"));
  });
});
