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

const { main } = await import("../actions/run-instances.js");

describe("run-instances", () => {
  it("should log the response from the EC2 run instances command", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      Instances: [
        {
          InstanceId: "i-0e8810a92833675aa",
        },
      ],
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith({
      Instances: [
        {
          InstanceId: "i-0e8810a92833675aa",
        },
      ],
    });
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to launch instances"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to launch instances"),
    );
  });
});
