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

const { main } = await import("../actions/create-key-pair.js");

describe("create-key-pair", () => {
  it("should log the key material", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({
      KeyMaterial: "foo",
    });

    await main({ keyName: "test" });

    expect(logSpy).toHaveBeenCalledWith("foo");
  });

  it("should log InvalidKeyPair.Duplicate errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("InvalidKeyPair");
    error.name = "InvalidKeyPair.Duplicate";

    send.mockRejectedValueOnce(error);

    await main({ keyName: "keyName" });

    expect(logSpy).toBeCalledWith("InvalidKeyPair. Try another key name.");
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Failed to create key pair");
    send.mockRejectedValueOnce(error);
    await expect(() => main({ keyName: "test" })).rejects.toBe(error);
  });
});
