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

const { main } = await import("../actions/terminate-instances.js");

describe("terminate-instances", () => {
  it("should log the instances that were terminated", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      TerminatingInstances: [
        {
          InstanceId: "bar",
        },
      ],
    });

    await main();

    expect(logSpy).toHaveBeenNthCalledWith(1, "Terminating instances:");
    expect(logSpy).toHaveBeenNthCalledWith(2, " • bar");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to terminate instances"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to terminate instances"),
    );
  });
});
