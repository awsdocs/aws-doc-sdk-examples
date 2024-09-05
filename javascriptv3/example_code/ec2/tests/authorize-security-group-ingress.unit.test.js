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

const { main } = await import("../actions/authorize-security-group-ingress.js");

describe("authorize-security-group-ingress", () => {
  it("should log the formatted security group rules", async () => {
    const logSpy = vi.spyOn(console, "log");
    const rules = [{ foo: "bar" }];
    send.mockResolvedValueOnce({
      SecurityGroupRules: rules,
    });

    await main({ groupId: "123", ipAddress: "123" });

    expect(logSpy).toHaveBeenCalledWith(JSON.stringify(rules, null, 2));
  });

  it("should log InvalidGroupId.Malformed errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("InvalidGroupId");
    error.name = "InvalidGroupId.Malformed";

    send.mockRejectedValueOnce(error);

    await main({ groupId: "groupId", ipAddress: "ipAddress" });

    expect(logSpy).toBeCalledWith(
      "InvalidGroupId. Please provide a valid GroupId.",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Failed to authorize security group");
    send.mockRejectedValueOnce(error);

    await expect(() => main({ groupId: "123", ipAddress: "123" })).rejects.toBe(
      error,
    );
  });
});
