// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  CreateAssetCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/create-asset.js";

describe("createAsset", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create an asset successfully", async () => {
    const mockResult = { assetId: "0123456789ab", assetArn: "abcdefghijk" };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        value: mockResult,
      });

    const result = await main({
      assetName: "test-asset-name",
      assetModelId: "test-asset-model-id",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateAssetCommand));
    expect(result.value).toEqual(mockResult);
  });

  it("should handle ResourceNotFound error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "ResourceNotFound";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      assetName: "test-asset-name",
      assetModelId: "test-asset-model-id",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateAssetCommand));
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
        assetName: "test-asset-name",
        assetModelId: "test-asset-model-id",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateAssetCommand));
  });
});
