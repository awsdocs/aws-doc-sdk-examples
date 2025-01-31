// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { UpdateOpsItemCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/update-ops-item.js";

describe("updateOpsItem", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should update an OpsItem successfully", async () => {
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({});

    const result = await main({ opsItemId: "123", status: "Open" });

    expect(sendMock).toHaveBeenCalledWith(expect.any(UpdateOpsItemCommand));
    expect(sendMock).toHaveBeenCalledWith(
      expect.objectContaining({
        input: {
          OpsItemId: "123",
          Status: "Open",
        },
      }),
    );
    expect(result).toEqual({ Success: true });
  });

  it("should handle MissingParameter error", async () => {
    const mockError = new Error("testError");
    mockError.name = "OpsItemLimitExceededException";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({ opsItemId: "123" });

    expect(sendMock).toHaveBeenCalledWith(expect.any(UpdateOpsItemCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `Couldn't create ops item because you have exceeded your open OpsItem limit. ${mockError.message}.`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(main({ opsItemId: "123", status: "Open" })).rejects.toThrow(
      mockError,
    );

    expect(sendMock).toHaveBeenCalledWith(expect.any(UpdateOpsItemCommand));
  });
});
