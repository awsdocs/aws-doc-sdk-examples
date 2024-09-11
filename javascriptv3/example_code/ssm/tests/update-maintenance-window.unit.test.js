// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { UpdateMaintenanceWindowCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/update-maintenance-window.js";

describe("updateMaintenanceWindow", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should update a maintenance window successfully", async () => {
    const mockOpsItemArn = "arn:aws:ssm:us-west-2:123456789012:opsitem/123";
    const mockOpsItemId = "123";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({
        opsItemArn: mockOpsItemArn,
        opsItemId: mockOpsItemId,
      });

    const result = await main({
      windowId: "test-window-id",
      allowUnassociatedTargets: true,
      duration: 2,
      enabled: true,
      name: "updated-test-window",
      schedule: "cron(0 0 ? * MON *)",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(UpdateMaintenanceWindowCommand),
    );
    expect(sendMock).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          WindowId: "test-window-id",
          AllowUnassociatedTargets: true,
          Duration: 2,
          Enabled: true,
          Name: "updated-test-window",
          Schedule: "cron(0 0 ? * MON *)",
        },
      }),
    );
    expect(result).toEqual({
      OpsItemArn: mockOpsItemArn,
      OpsItemId: mockOpsItemId,
    });
  });

  it("should handle ValidationError", async () => {
    const mockError = new Error(
      "ValidationError: Invalid maintenance window parameters.",
    );
    mockError.name = "ValidationError";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      windowId: "test-window-id",
      allowUnassociatedTargets: true,
      duration: 2,
      enabled: true,
      name: "updated-test-window",
      schedule: "invalid-schedule",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(UpdateMaintenanceWindowCommand),
    );
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Are these values correct?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        windowId: "test-window-id",
        allowUnassociatedTargets: true,
        duration: 2,
        enabled: true,
        name: "updated-test-window",
        schedule: "cron(0 0 ? * MON *)",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(UpdateMaintenanceWindowCommand),
    );
  });
});
