// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  GetAssetPropertyValueCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/get-asset-property-value.js";

describe("getAssetPropertyValue", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should get an asset property value successfully", async () => {
    const mockPropertyValue = {
      successEntries: [{ entryId: "mock-entry-id" }],
    };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        value: mockPropertyValue,
      });

    const result = await main({
      entryId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(GetAssetPropertyValueCommand),
    );
    expect(result.value).toEqual(mockPropertyValue);
  });

  it("should handle ResourceNotFound error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "ResourceNotFound";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      entryId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(GetAssetPropertyValueCommand),
    );
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. The asset property entry could not be found. Please check the entry id.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        entryId: "1234567890ab",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(GetAssetPropertyValueCommand),
    );
  });
});
