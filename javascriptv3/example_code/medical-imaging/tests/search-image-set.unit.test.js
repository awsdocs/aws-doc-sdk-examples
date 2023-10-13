/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const paginateSearchImageSets = vi.fn();

vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");

  return {
    ...actual,
    paginateSearchImageSets,
  };
});

const { searchImageSets } = await import("../actions/search-image-sets.js");

describe("search-image-sets", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const filters = [
      {
        values: [
          { createdAt: new Date("1985-04-12T23:20:50.52Z") },
          { createdAt: new Date("2023-09-12T23:20:50.52Z") },
        ],
        operator: "BETWEEN",
      },
    ];

    const response = {
      $metadata: {
        httpStatusCode: 200,
        requestId: "f009ea9c-84ca-4749-b5b6-7164f00a5ada",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      imageSetsMetadataSummaries: [
        {
          DICOMTags: [Object],
          createdAt: "2023-09-19T16:59:40.551Z",
          imageSetId: "7f75e1b5c0f40eac2b24cf712f485f50",
          updatedAt: "2023-09-19T16:59:40.551Z",
          version: 1,
        },
      ],
    };

    paginateSearchImageSets.mockImplementationOnce(async function* () {
      yield response;
    });

    await searchImageSets(datastoreId, filters);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
