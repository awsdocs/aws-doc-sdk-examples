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

const { main } = await import("../actions/unmonitor-instances.js");

describe("unmonitor-instances", () => {
  it("should log a list instance monitoring details", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({
      InstanceMonitorings: [
        { InstanceId: "foo", Monitoring: { State: "bar" } },
      ],
    });

    await main({ instanceIds: ["i-123"] });

    expect(logSpy).toHaveBeenNthCalledWith(1, "Monitoring status:");
    expect(logSpy).toHaveBeenNthCalledWith(
      2,
      " • Detailed monitoring state for foo is bar.",
    );
  });

  it("should log InvalidInstanceID.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Failed to unmonitor instances");
    error.name = "InvalidInstanceID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({ instanceIds: ["i-123"] });

    expect(logSpy).toHaveBeenCalledWith("Failed to unmonitor instances");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
