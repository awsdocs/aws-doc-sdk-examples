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

const { main } = await import("../actions/describe-security-groups.js");

describe("describe-security-groups", () => {
  it("should log the security group info", async () => {
    const logSpy = vi.spyOn(console, "log");
    const securityGroups = [
      {
        Foo: "bar",
      },
    ];

    send.mockResolvedValueOnce({
      SecurityGroups: securityGroups,
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      JSON.stringify(securityGroups, null, 2),
    );
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to describe security group"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to describe security group"),
    );
  });
});
