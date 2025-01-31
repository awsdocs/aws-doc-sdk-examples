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

const { main } = await import("../actions/delete-key-pair.js");

describe("delete-key-pair", () => {
  it("should log a success message", async () => {
    const logSpy = vi.spyOn(console, "log");
    send.mockResolvedValueOnce({});

    await main({ keyName: "name" });
    expect(logSpy).toHaveBeenCalledWith("Successfully deleted key pair.");
  });

  it("should log MissingParameter errors", async () => {
    const logSpy = vi.spyOn(console, "warn");
    const error = new Error("MissingParameter");
    error.name = "MissingParameter";

    send.mockRejectedValueOnce(error);

    await main({ keyName: "test" });

    expect(logSpy).toBeCalledWith(
      "MissingParameter. Did you provide the required value?",
    );
  });

  it("should throw unknown errors", async () => {
    const error = new Error("Failed to delete key pair");
    send.mockRejectedValueOnce(error);

    await expect(() => main({ keyName: "name" })).rejects.toBe(error);
  });
});
