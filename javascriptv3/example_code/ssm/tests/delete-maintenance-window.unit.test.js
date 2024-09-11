// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { DeleteMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/delete-maintenance-window.js";

describe("deleteMaintenanceWindow", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should delete a maintenance window successfully", async () => {
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({});

    const result = await main({ windowId: "test-window-id" });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(DeleteMaintenanceWindowCommand),
    );
    expect(sendMock).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          WindowId: "test-window-id",
        },
      }),
    );
    expect(result).toEqual({ Deleted: true });
  });

  it("should handle MissingParameter error", async () => {
    const mockError = new Error("MissingParameter: Some parameter is missing.");
    mockError.name = "MissingParameter";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({});

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(DeleteMaintenanceWindowCommand),
    );
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide this value?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(main({ windowId: "test-window-id" })).rejects.toThrow(
      mockError,
    );

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(DeleteMaintenanceWindowCommand),
    );
  });
});
