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

import { main } from "../actions/create-key-pair.js";

describe("create-key-pair", () => {
  it("should log the key material", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      KeyMaterial: "foo",
    });

    await main();

    expect(logSpy).toHaveBeenCalledWith("foo");
  });

  it("should log the error message", async () => {
    const logSpy = vi.spyOn(console, "error");
    send.mockRejectedValueOnce(new Error("Failed to create key pair"));

    await main();

    expect(logSpy).toHaveBeenCalledWith(new Error("Failed to create key pair"));
  });
});
