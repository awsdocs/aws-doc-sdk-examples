// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";

const paginateDescribeOpsItems = vi.fn();

vi.doMock("@aws-sdk/client-ssm", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ssm");
  return {
    ...actual,
    paginateDescribeOpsItems,
  };
});

const { main } = await import("../actions/describe-ops-items.js");

describe("describeOpsItems", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should describe OpsItems successfully", async () => {
    const mockOpsItemSummaries = [
      { OpsItemId: "123", Title: "Test OpsItem" },
      { OpsItemId: "456", Title: "Another OpsItem" },
    ];

    paginateDescribeOpsItems.mockImplementationOnce(async function* () {
      yield { OpsItemSummaries: mockOpsItemSummaries };
    });

    const result = await main({ opsItemId: "123" });

    expect(result.OpsItemSummaries).toEqual(mockOpsItemSummaries);
  });

  it("should handle MissingParameter error", async () => {
    const mockError = new Error("MissingParameter: Some parameter is missing.");
    mockError.name = "MissingParameter";

    paginateDescribeOpsItems.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator function
      (async function* () {
        throw mockError;
      })(),
    );

    const consoleWarnSpy = vi.spyOn(console, "warn");

    await expect(main({})).rejects.toThrow(mockError);

    expect(consoleWarnSpy).toHaveBeenCalledWith(
      `${mockError.message}. Did you provide this value?`,
    );
  });

  it("should throw any other errors", async () => {
    const mockError = new Error("Something went wrong");
    paginateDescribeOpsItems.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator function
      (async function* () {
        throw mockError;
      })(),
    );

    await expect(main({})).rejects.toThrow(mockError);
  });
});
