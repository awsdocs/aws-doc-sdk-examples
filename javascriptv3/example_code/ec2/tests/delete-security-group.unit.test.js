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

const { main } = await import("../actions/delete-security-group.js");

describe("delete-security-group", () => {
  it("should log a success message", async () => {
    const logSpy = vi.spyOn(console, "log");

    send.mockResolvedValueOnce({});

    await main({ groupId: "abc" });

    expect(logSpy).toHaveBeenCalledWith("Security group deleted successfully.");
  });

  it("should log InvalidGroupId.Malformed errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("InvalidGroupId.Malformed");
    error.name = "InvalidGroupId.Malformed";

    send.mockRejectedValueOnce(error);

    await main({ groupId: "abc" });

    expect(logSpy).toBeCalledWith(
      "InvalidGroupId.Malformed. Please provide a valid GroupId.",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown");
    send.mockRejectedValueOnce(error);

    await expect(() => main({ groupId: "abc" })).rejects.toBe(error);
  });
});
