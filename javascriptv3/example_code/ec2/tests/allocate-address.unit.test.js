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

    expect(logSpy).toHaveBeenNthCalledWith(2, "ID: bar Public IP: foo");
  });

  it("should log MissingParameter errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Missing param");
    error.name = "MissingParameter";

    send.mockRejectedValueOnce(error);

    await main();

    expect(logSpy).toBeCalledWith(
      "Missing param. Did you provide these values?",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Failed to allocate address");
    send.mockRejectedValueOnce(error);

    await expect(main()).rejects.toBe(error);
  });
});
