// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  DeleteAssetModelCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/delete-asset-model.js";

describe("deleteAssetModel", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should delete an asset model successfully", async () => {
    const mockAssetModelResponse = { assetModelDeleted: true };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        mockAssetModelResponse,
      });

    const result = await main("mock-asset-model-id");

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteAssetModelCommand));
    expect(result.assetModelDeleted).toEqual(
      mockAssetModelResponse.assetModelDeleted,
    );
  });

  it("should handle ResourceNotFound error", async () => {
    const mockError = new Error("Test ResourceNotFound error");
    mockError.name = "ResourceNotFound";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main([]);

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteAssetModelCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. There was a problem deleting the asset model.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(main([])).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteAssetModelCommand));
  });
});
