// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  DescribeGatewayCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/describe-gateway.js";

describe("describeGateway", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should describe a Gateway successfully", async () => {
    const mockGatewayDescription = { gatewayArn: "0123456789ab" };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        gatewayDescription: mockGatewayDescription,
      });

    const result = await main({
      gatewayId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DescribeGatewayCommand));
    expect(result.gatewayDescription).toEqual(mockGatewayDescription);
  });

  it("should handle ResourceNotFound error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "ResourceNotFound";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      gatewayId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DescribeGatewayCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. The Gateway could not be found. Please check the Gateway Id.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        gatewayId: "1234567890ab",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(DescribeGatewayCommand));
  });
});
