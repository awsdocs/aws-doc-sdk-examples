// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { CreateMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/create-maintenance-window.js";

describe("createMaintenanceWindow", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create a new maintenance window successfully", async () => {
    const mockWindowId = "test-window-id";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({
        windowId: mockWindowId,
      });

    const result = await main({
      name: "test-window",
      description: "Test maintenance window",
      allowUnassociatedTargets: true,
      duration: 2,
      cutoff: 1,
      schedule: "cron(0 0 ? * MON *)",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(CreateMaintenanceWindowCommand),
    );
    expect(sendMock).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          Name: "test-window",
          Description: "Test maintenance window",
          AllowUnassociatedTargets: true,
          Duration: 2,
          Cutoff: 1,
          Schedule: "cron(0 0 ? * MON *)",
        },
      }),
    );
    expect(result.WindowId).toEqual(mockWindowId);
  });

  it("should handle MissingParameter error", async () => {
    const mockError = new Error("MissingParameter: Some parameter is missing.");
    mockError.name = "MissingParameter";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      name: "test-window",
      allowUnassociatedTargets: true,
      duration: 2,
      cutoff: 1,
      schedule: "cron(0 0 ? * MON *)",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(CreateMaintenanceWindowCommand),
    );
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide these values?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        name: "test-window",
        allowUnassociatedTargets: true,
        duration: 2,
        cutoff: 1,
        schedule: "cron(0 0 ? * MON *)",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(CreateMaintenanceWindowCommand),
    );
  });
});
