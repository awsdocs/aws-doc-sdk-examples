// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  DeleteGatewayCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/delete-gateway.js";

describe("deleteGateway", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should delete a Gateway successfully", async () => {
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({});

    const result = await main({
      gatewayId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteGatewayCommand));
    expect(result.gatewayDeleted).toEqual(true);
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

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteGatewayCommand));
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

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteGatewayCommand));
  });
});
