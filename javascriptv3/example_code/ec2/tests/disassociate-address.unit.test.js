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

const { main } = await import("../actions/disassociate-address.js");

describe("disassociate-address", () => {
  it("should log a success message", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({});

    await main({ associationId: "123" });

    expect(logSpy).toHaveBeenCalledWith("Successfully disassociated address");
  });

  it("should log InvalidAssociationID.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Failed to disassociate address");
    error.name = "InvalidAssociationID.NotFound";
    send.mockRejectedValueOnce(error);

    await main({ associationId: "123" });

    expect(logSpy).toHaveBeenCalledWith("Failed to disassociate address.");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
