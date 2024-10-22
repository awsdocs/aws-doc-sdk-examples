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

const { main } = await import("../actions/describe-security-groups.js");

describe("describe-security-groups", () => {
  it("should log the security group info", async () => {
    const logSpy = vi.spyOn(console, "log");
    const securityGroups = [
      {
        GroupName: "group",
        GroupId: "sg-1",
        Description: "My security group",
      },
    ];

    send.mockResolvedValueOnce({
      SecurityGroups: securityGroups,
    });

    await main({});

    expect(logSpy).toHaveBeenCalledWith(
      "Security groups:\n• group (sg-1): My security group",
    );
  });

  it("should log InvalidGroupId.Malformed errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Bad group id");
    error.name = "InvalidGroupId.Malformed";
    send.mockRejectedValueOnce(error);

    await main({});

    expect(logSpy).toHaveBeenCalledWith(
      "Bad group id. Please provide a valid GroupId.",
    );
  });

  it("should log InvalidGroup.NotFound errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("Missing");
    error.name = "InvalidGroup.NotFound";
    send.mockRejectedValueOnce(error);

    await main({});

    expect(logSpy).toHaveBeenCalledWith("Missing");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Unknown error");
    send.mockRejectedValueOnce(error);

    await expect(() => main({})).rejects.toBe(error);
  });
});
