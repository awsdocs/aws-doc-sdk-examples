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

const { main } = await import("../actions/describe-instances.js");

describe("describe-instances", () => {
  it("should log found instances", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      Reservations: [
        {
          Instances: [
            {
              InstanceId: "123",
            },
          ],
        },
      ],
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith([{ InstanceId: "123" }]);
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to describe instances"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to describe instances"),
    );
  });
});
