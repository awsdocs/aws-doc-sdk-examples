// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { SendCommandCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/send-command.js";

describe("sendCommand", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should send a command successfully", async () => {
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({});

    const result = await main({ documentName: "valid-document" });

    expect(sendMock).toHaveBeenCalledWith(expect.any(SendCommandCommand));
    expect(sendMock).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          DocumentName: "valid-document",
        },
      }),
    );
    expect(result).toEqual({ Success: true });
  });

  it("should handle ValidationError", async () => {
    const mockError = new Error("ValidationError: Invalid document name.");
    mockError.name = "ValidationError";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({ documentName: "invalid-document" });

    expect(sendMock).toHaveBeenCalledWith(expect.any(SendCommandCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide a valid document name?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(main({ documentName: "valid-document" })).rejects.toThrow(
      mockError,
    );

    expect(sendMock).toHaveBeenCalledWith(expect.any(SendCommandCommand));
  });
});
