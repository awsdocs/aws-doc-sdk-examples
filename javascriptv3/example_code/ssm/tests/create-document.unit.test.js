// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";
import { CreateDocumentCommand, SSMClient } from "@aws-sdk/client-ssm";
import { main } from "../actions/create-document.js";

describe("createDocument", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should create a new document successfully", async () => {
    const mockDocumentDescription = { Name: "test-document" };
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockResolvedValueOnce({
        documentDescription: mockDocumentDescription,
      });

    const result = await main({
      content: "document content",
      name: "test-document",
      documentType: "Command",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateDocumentCommand));
    expect(result.DocumentDescription).toEqual(mockDocumentDescription);
  });

  it("should handle DocumentAlreadyExists error", async () => {
    const mockError = new Error("Document already exists");
    mockError.name = "DocumentAlreadyExists";
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);
    const consoleWarnSpy = vi.spyOn(console, "warn");

    await main({
      content: "document content",
      name: "test-document",
      documentType: "Command",
    });

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateDocumentCommand));
    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide a new document name?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    const sendMock = vi
      .spyOn(SSMClient.prototype, "send")
      .mockRejectedValueOnce(mockError);

    await expect(
      main({
        content: "document content",
        name: "test-document",
        documentType: "Command",
      }),
    ).rejects.toThrow(mockError);

    expect(sendMock).toHaveBeenCalledWith(expect.any(CreateDocumentCommand));
  });
});
