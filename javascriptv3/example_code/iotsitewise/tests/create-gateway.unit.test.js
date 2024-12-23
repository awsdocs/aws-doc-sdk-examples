// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  CreateGatewayCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/create-gateway.js";

describe("createGateway", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create a Gateway successfully", async () => {
    const mockGatewayDescription = {
      gatewayArn: "0123456789ab",
      gatewayId: "abcdefghijk",
    };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        gatewayDescription: mockGatewayDescription,
      });

    const result = await main({
      gatewayName: "test-name",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateGatewayCommand));
    expect(result.gatewayDescription).toEqual(mockGatewayDescription);
  });

  it("should handle IoTSiteWiseError error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "IoTSiteWiseError";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      gatewayName: "test-name",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateGatewayCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. There was a problem creating the Gateway.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        gatewayName: "test-name",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateGatewayCommand));
  });
});
