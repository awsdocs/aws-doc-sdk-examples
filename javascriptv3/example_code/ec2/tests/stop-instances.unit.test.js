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

import { main } from "../actions/stop-instances.js";

describe("stop-instances", () => {
  it("should log the instances that were stopped", async () => {
    const logSpy = vi.spyOn(console, "log");
    const instances = [
      {
        InstanceId: "bar",
      },
    ];

    send.mockResolvedValueOnce({
      StoppingInstances: instances,
    });

    await main();

    expect(logSpy).toHaveBeenNthCalledWith(1, "Stopping instances:");
    expect(logSpy).toHaveBeenNthCalledWith(2, " • bar");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to stop instances"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(new Error("Failed to stop instances"));
  });
});
