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

const { main } = await import("../actions/create-security-group.js");

describe("create-security-group", () => {
  it("should log the security group id", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      GroupId: "foo",
    });

    await main({ groupName: "name", description: "description" });

    expect(logSpy).toHaveBeenCalledWith("foo");
  });

  it("should log InvalidParameterValue errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("InvalidParameterValue");
    error.name = "InvalidParameterValue";

    send.mockRejectedValueOnce(error);

    await main({ groupName: "sg-1" });

    expect(logSpy).toBeCalledWith("InvalidParameterValue.");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Failed to create security group");
    send.mockRejectedValueOnce(error);

    await expect(() =>
      main({ groupName: "name", description: "description" }),
    ).rejects.toBe(error);
  });
});
