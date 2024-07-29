// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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

const { main } = await import("../actions/describe-regions.js");

describe("describe-regions", () => {
  it("should log a list of regions", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({
      Regions: [{ RegionName: "foo" }],
    });

    await main({});

    expect(logSpy).toHaveBeenNthCalledWith(1, "Found regions:");
    expect(logSpy).toHaveBeenNthCalledWith(2, " • foo");
  });

  it("should log DryRunOperation errors", async () => {
    const logSpy = vi.spyOn(console, "log");
    const error = new Error("Would have run.");
    error.name = "DryRunOperation";
    send.mockRejectedValueOnce(error);

    await main({ dryRun: true });

    expect(logSpy).toHaveBeenCalledWith("Would have run.");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
