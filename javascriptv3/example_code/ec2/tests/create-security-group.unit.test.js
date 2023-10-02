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

const { main } = await import("../actions/create-security-group.js");

describe("create-security-group", () => {
  it("should log the security group id", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      GroupId: "foo",
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith("foo");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to create security group"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to create security group"),
    );
  });
});
