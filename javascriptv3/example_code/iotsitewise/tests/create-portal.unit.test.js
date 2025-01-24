// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import {
  CreatePortalCommand,
  IoTSiteWiseClient,
} from "@aws-sdk/client-iotsitewise";
import { main } from "../actions/create-portal.js";

describe("createGateway", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create a Portal successfully", async () => {
    const mockPortalDescription = {
      portalId: "0123456789ab",
      portalArn: "abcdefghijk",
      portalStartUrl: "hijklmnop",
      portalStatus: {
        state: "ACTIVE",
      },
      ssoApplicationId: "abcdefghijk",
    };
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockResolvedValueOnce({
        portalDescription: mockPortalDescription,
      });

    const result = await main({
      portalName: "test-portal-name",
      portalContactEmail: "test@example.com",
      roleArn: "test-role-arn",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreatePortalCommand));
    expect(result.portalDescription).toEqual(mockPortalDescription);
  });

  it("should handle IoTSiteWiseError error", async () => {
    const mockError = new Error("Resource not found");
    mockError.name = "IoTSiteWiseError";
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      portalName: "test-portal-name",
      portalContactEmail: "test@example.com",
      roleArn: "test-role-arn",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreatePortalCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. There was a problem creating the Portal.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(IoTSiteWiseClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        portalName: "test-portal-name",
        portalContactEmail: "test@example.com",
        roleArn: "test-role-arn",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreatePortalCommand));
  });
});
