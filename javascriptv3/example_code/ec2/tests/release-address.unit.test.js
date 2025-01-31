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

const { main } = await import("../actions/release-address.js");

describe("release-address", () => {
  it("should log a success message", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({});

    await main({ allocationId: "123" });

    expect(logSpy).toHaveBeenCalledWith("Successfully released address.");
  });

  it("should log InvalidAllocationID.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Failed to release address");
    error.name = "InvalidAllocationID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({ allocationId: "123" });

    expect(logSpy).toHaveBeenCalledWith(
      "Failed to release address. Please provide a valid AllocationID.",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
