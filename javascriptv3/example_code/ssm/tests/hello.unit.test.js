// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, it, expect, vi, beforeEach } from "vitest";

const paginateListDocuments = vi.fn();

vi.doMock("@aws-sdk/client-ssm", async () => {
  const actual = await vi.importActual("@aws-sdk/client-ssm");
  return {
    ...actual,
    paginateListDocuments,
  };
});

const { main } = await import("../hello.js");

describe("hello", () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it("should list some documents", async () => {
    const mockListDocuments = [{ Name: "test-doc-1" }, { Name: "test-doc-2" }];
    const consoleSpy = vi.spyOn(console, "log");

    paginateListDocuments.mockImplementationOnce(async function* () {
      yield { DocumentIdentifiers: mockListDocuments };
    });

    await main();

    expect(consoleSpy).toHaveBeenCalledWith(mockListDocuments);
  });

  it("should throw any errors", async () => {
    const mockError = new Error("Something went wrong");
    paginateListDocuments.mockReturnValueOnce(
      // eslint-disable-next-line require-yield
      (async function* () {
        throw mockError;
      })()
    );

    await expect(main({})).rejects.toThrow(mockError);
  });
});
