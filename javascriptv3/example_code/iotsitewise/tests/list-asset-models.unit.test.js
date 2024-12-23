// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  ListAssetModelsCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/list-asset-models.js";

describe("listAssetModels", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should describe list asset models successfully", async () => {
    const mockAssetModelTypes = [];
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        mockAssetModelTypes,
      });

    const result = await main([]);

    expect(sendMock).toHaveBeenCalledWith(expect.any(ListAssetModelsCommand));
    expect(result.mockAssetModelTypes).toEqual(mockAssetModelTypes);
  });

  it("should handle IoTSiteWiseError error", async () => {
    const mockError = new Error("Test IoTSiteWiseError");
    mockError.name = "IoTSiteWiseError";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main([]);

    expect(sendMock).toHaveBeenCalledWith(expect.any(ListAssetModelsCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. There was a problem listing the asset model types.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(main([])).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(ListAssetModelsCommand));
  });
});
