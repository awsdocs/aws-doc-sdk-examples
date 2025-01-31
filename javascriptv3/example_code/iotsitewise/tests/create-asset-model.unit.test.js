// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  CreateAssetModelCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/create-asset-model.js";

describe("createAssetModel", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create an asset model successfully", async () => {
    const mockResult = {
      assetModelId: "0123456789ab",
      assetModelArn: "abcdefghijk",
    };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        value: mockResult,
      });

    const result = await main({
      assetModelName: "test-asset-name",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateAssetModelCommand));
    expect(result.value).toEqual(mockResult);
  });

  it("should handle IoTSiteWise error", async () => {
    const mockError = new Error("IoTSiteWise error");
    mockError.name = "IoTSiteWiseError";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      assetModelName: "test-asset-name",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateAssetModelCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. There was a problem creating the asset model.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        assetModelName: "test-asset-name",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateAssetModelCommand));
  });
});
