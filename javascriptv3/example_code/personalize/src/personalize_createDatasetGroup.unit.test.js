// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi } from "vitest";

const mockSend = vi
  .fn()
  .mockResolvedValue({ datasetGroupArn: "mock-dataset-group-arn" });

vi.doMock("@aws-sdk/client-personalize", async () => ({
  ...(await vi.importActual("@aws-sdk/client-personalize")),
  PersonalizeClient: vi.fn().mockReturnValue({ send: mockSend }),
}));

const { run, createDatasetGroupParam } = await import(
  "./personalize_createDatasetGroup.js"
);

describe("createDatasetGroup", () => {
  it("should call 'send' on the client", async () => {
    const response = await run(createDatasetGroupParam);
    expect(response).toBe("Run successfully");
    expect(mockSend).toHaveBeenCalled();
  });

  it("should handle errors", async () => {
    // Mock the PersonalizeClient and the send method to throw an error
    mockSend.mockRejectedValue(new Error("mock error"));

    const consoleSpy = vi.spyOn(console, "log").mockImplementation(() => {});
    await run(createDatasetGroupParam);

    expect(consoleSpy).toHaveBeenCalledWith("Error", new Error("mock error"));
    consoleSpy.mockRestore();
  });
});
