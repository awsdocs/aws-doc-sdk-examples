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

const { main } = await import("../actions/describe-key-pairs.js");

describe("describe-key-pairs", () => {
  it("should log the returned key pairs", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      KeyPairs: [
        {
          KeyName: "foo",
          KeyPairId: "bar",
        },
      ],
    });

    await main();

    expect(logSpy).nthCalledWith(
      1,
      "The following key pairs were found in your account:",
    );
    expect(logSpy).nthCalledWith(2, " • bar: foo");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to describe key pairs"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(
      new Error("Failed to describe key pairs"),
    );
  });
});
