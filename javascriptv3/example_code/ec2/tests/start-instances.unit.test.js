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

const { main } = await import("../actions/start-instances.js");

describe("start-instances", () => {
  it("should log the instances that were started", async () => {
    const logSpy = vi.spyOn(console, "log");
    const instances = [
      {
        InstanceId: "i-123",
      },
    ];

    send.mockResolvedValueOnce({
      StartingInstances: instances,
    });

    await main({ instanceIds: ["i-123"] });

    expect(logSpy).toHaveBeenNthCalledWith(1, "Starting instances:");
    expect(logSpy).toHaveBeenNthCalledWith(2, " • i-123");
  });

  it("should log InvalidInstanceID.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Failed to start instances");
    error.name = "InvalidInstanceID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({ instanceIds: ["i-123"] });

    expect(logSpy).toHaveBeenCalledWith("Failed to start instances");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
