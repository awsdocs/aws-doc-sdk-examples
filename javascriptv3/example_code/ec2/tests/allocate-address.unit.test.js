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

const { main } = await import("../actions/allocate-address.js");

describe("allocate-address", () => {
  it("should log id of the created address and the address itself", async () => {
    const logSpy = vi.spyOn(console, "log");
    const response = {
      PublicIp: "foo",
      AllocationId: "bar",
    };

    send.mockResolvedValueOnce(response);

    await main();

    expect(logSpy).toHaveBeenNthCalledWith(2, `ID: bar Public IP: foo`);
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to allocate address"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to allocate address"),
    );
  });
});
