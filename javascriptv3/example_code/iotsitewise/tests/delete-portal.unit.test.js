// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  DeletePortalCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/delete-portal.js";

describe("deletePortal", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should delete a Portal successfully", async () => {
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({});

    const result = await main({
      portalId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeletePortalCommand));
    expect(result.portalDeleted).toEqual(true);
  });

  it("should handle ResourceNotFound error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "ResourceNotFound";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      portalId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeletePortalCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. There was a problem deleting the portal. Please check the portal id.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        portalId: "1234567890ab",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeletePortalCommand));
  });
});
