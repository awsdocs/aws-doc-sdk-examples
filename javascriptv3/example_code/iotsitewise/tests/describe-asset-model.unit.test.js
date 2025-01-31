// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  DescribeAssetModelCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/describe-asset-model.js";

describe("describeAssetModel", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should describe an asset model successfully", async () => {
    const mockAssetModelDescription = { assetModelArn: "0123456789ab" };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        assetModelDescription: mockAssetModelDescription,
      });

    const result = await main({
      assetModelId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(DescribeAssetModelCommand),
    );
    expect(result.assetModelDescription).toEqual(mockAssetModelDescription);
  });

  it("should handle ResourceNotFound error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "ResourceNotFound";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      assetModelId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(DescribeAssetModelCommand),
    );
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. The asset model could not be found. Please check the asset model id.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        assetModelId: "1234567890ab",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(DescribeAssetModelCommand),
    );
  });
});
