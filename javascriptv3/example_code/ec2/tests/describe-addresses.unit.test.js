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

const { main } = await import("../actions/describe-addresses.js");

describe("describe-addresses", () => {
  it("should log the instances that were stopped", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({
      Addresses: [
        {
          PublicIp: "foo",
        },
      ],
    });

    await main({ allocationId: "123" });

    expect(logSpy).toHaveBeenNthCalledWith(1, "Elastic IP addresses:");
    expect(logSpy).toHaveBeenNthCalledWith(2, " • foo");
  });

  it("should log InvalidAllocationID.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("AllocationId not found");
    error.name = "InvalidAllocationID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({ allocationId: "123" });

    expect(logSpy).toHaveBeenCalledWith(
      "AllocationId not found. Please provide a valid AllocationId.",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({ keyName: "name" })).rejects.toBe(error);
  });
});
