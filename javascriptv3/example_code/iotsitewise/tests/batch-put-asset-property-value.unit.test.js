// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  BatchPutAssetPropertyValueCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/batch-put-asset-property-value.js";

describe("batchPutAssetPropertyValue", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should put asset property values successfully", async () => {
    const mockResult = {};
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        value: mockResult,
      });

    const result = await main({
      entries: [
        {
          entryId: "mock-entry-id",
          propertyValues: [
            {
              value: {
                stringValue: "mock-entry-value",
              },
            },
          ],
        },
      ],
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(BatchPutAssetPropertyValueCommand),
    );
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
      entries: [
        {
          entryId: "mock-entry-id",
          propertyValues: [
            {
              value: {
                stringValue: "mock-entry-value",
              },
            },
          ],
        },
      ],
    });

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(BatchPutAssetPropertyValueCommand),
    );
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. A resource could not be found.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        entries: [
          {
            entryId: "mock-entry-id",
            propertyValues: [
              {
                value: {
                  stringValue: "mock-entry-value",
                },
              },
            ],
          },
        ],
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(
      expect.any(BatchPutAssetPropertyValueCommand),
    );
  });
});
