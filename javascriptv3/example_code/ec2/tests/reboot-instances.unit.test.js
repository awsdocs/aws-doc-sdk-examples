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

const { main } = await import("../actions/reboot-instances.js");

describe("reboot-instances", () => {
  it("should log a success message", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({});

    await main({ instanceIds: [] });

    expect(logSpy).toHaveBeenCalledWith("Instance rebooted successfully.");
  });

  it("should log InvalidInstanceID.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Failed");
    error.name = "InvalidInstanceID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({ instanceIds: [] });

    expect(logSpy).toHaveBeenCalledWith(
      "Failed. Please provide the InstanceId of a valid instance to reboot.",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
