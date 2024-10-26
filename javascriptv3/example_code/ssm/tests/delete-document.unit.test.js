// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { DeleteDocumentCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/delete-document.js";

describe("deleteDocument", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should delete a document successfully", async () => {
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({});

    const result = await main({
      documentName: "test-document",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteDocumentCommand));
    expect(result).toEqual({ Deleted: true });
  });

  it("should handle MissingParameter error", async () => {
    const mockError = new Error("MissingParameter: Some parameter is missing.");
    mockError.name = "MissingParameter";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({});

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteDocumentCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide this value?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(main({ documentName: "test-document" })).rejects.toThrow(
      mockError,
    );

    expect(sendMock).toHaveBeenCalledWith(expect.any(DeleteDocumentCommand));
  });
});
