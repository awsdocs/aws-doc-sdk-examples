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

const { main } = await import("../actions/monitor-instances.js");

describe("monitor-instances", () => {
  it("should log a list instance monitoring details", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({
      InstanceMonitorings: [
        { InstanceId: "foo", Monitoring: { State: "bar" } },
      ],
    });

    await main();

    expect(logSpy).toHaveBeenNthCalledWith(1, "Monitoring status:");
    expect(logSpy).toHaveBeenNthCalledWith(
      2,
      " • Detailed monitoring state for foo is bar.",
    );
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(new Error("Failed"));
  });
});
