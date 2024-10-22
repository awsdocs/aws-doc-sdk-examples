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
    const message =
      "Hello, AWS Systems Manager! Let's list some of your documents:\n";
    const docList = [
      "test-doc-1 - testFormat - testDate",
      "test-doc-2 - testFormat - testDate",
    ];

    const mockListDocuments = [
      {
        Name: "test-doc-1",
        DocumentFormat: "testFormat",
        CreatedDate: "testDate",
      },
      {
        Name: "test-doc-2",
        DocumentFormat: "testFormat",
        CreatedDate: "testDate",
      },
    ];
    const consoleSpy = vi.spyOn(console, "log");

    paginateListDocuments.mockImplementationOnce(async function* () {
      yield { DocumentIdentifiers: mockListDocuments };
    });

    await main();

    expect(consoleSpy).toHaveBeenCalledWith(message);
    expect(consoleSpy).toHaveBeenCalledWith(docList[0]);
    expect(consoleSpy).toHaveBeenCalledWith(docList[1]);
  });

  it("should throw any errors", async () => {
    const mockError = new Error("Something went wrong");
    paginateListDocuments.mockReturnValueOnce(
      // biome-ignore lint/correctness/useYield: Mock generator
      (async function* () {
        throw mockError;
      })(),
    );

    await expect(main({})).rejects.toThrow(mockError);
  });
});
