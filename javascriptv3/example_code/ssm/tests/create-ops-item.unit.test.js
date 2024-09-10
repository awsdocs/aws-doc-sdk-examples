// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { CreateOpsItemCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/create-ops-item.js";

describe("createOpsItem", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create a new OpsItem successfully", async () => {
    const mockOpsItemArn = "arn:aws:ssm:us-west-2:123456789012:opsitem/123";
    const mockOpsItemId = "123";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({
        opsItemArn: mockOpsItemArn,
        opsItemId: mockOpsItemId,
      });

    const result = await main({
      title: "Test OpsItem",
      source: "test-service",
      category: "Troubleshooting",
      severity: "2",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateOpsItemCommand));
    expect(sendMock).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          Title: "Test OpsItem",
          Source: "test-service",
          Category: "Troubleshooting",
          Severity: "2",
        },
      }),
    );
    expect(result).toEqual({
      OpsItemArn: mockOpsItemArn,
      OpsItemId: mockOpsItemId,
    });
  });

  it("should handle MissingParameter error", async () => {
    const mockError = new Error("MissingParameter: Some parameter is missing.");
    mockError.name = "MissingParameter";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      title: "Test OpsItem",
      source: "test-service",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateOpsItemCommand));
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
        title: "Test OpsItem",
        source: "test-service",
        category: "Troubleshooting",
        severity: "2",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateOpsItemCommand));
  });
});
