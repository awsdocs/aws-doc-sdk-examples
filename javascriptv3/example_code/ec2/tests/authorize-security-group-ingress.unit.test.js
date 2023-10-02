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

const { main } = await import("../actions/authorize-security-group-ingress.js");

describe("authorize-security-group-ingress", () => {
  it("should log the formatted security group rules", async () => {
    const logSpy = vi.spyOn(console, "log");
    const rules = [{ foo: "bar" }];
    send.mockResolvedValueOnce({
      SecurityGroupRules: rules,
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith(JSON.stringify(rules, null, 2));
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to authorize security group"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to authorize security group"),
    );
  });
});
