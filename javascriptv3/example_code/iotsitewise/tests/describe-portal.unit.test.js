// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  DescribePortalCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/describe-portal.js";

describe("describePortal", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should describe a Portal successfully", async () => {
    const mockPortalDescription = {
      portalArn: "01234567890ab",
      portalId: "abcdefghij",
    };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        mockPortalDescription,
      });

    const result = await main({
      portalId: "1234567890ab",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DescribePortalCommand));
    expect(result.mockPortalDescription).toEqual(mockPortalDescription);
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

    expect(sendMock).toHaveBeenCalledWith(expect.any(DescribePortalCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. The Portal could not be found. Please check the Portal Id.`,
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

    expect(sendMock).toHaveBeenCalledWith(expect.any(DescribePortalCommand));
  });
});
