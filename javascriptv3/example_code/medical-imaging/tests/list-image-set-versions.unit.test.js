/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const paginateListImageSetVersions = vi.fn();

vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");

  return {
    ...actual,
    paginateListImageSetVersions,
  };
});

const { listImageSetVersions } = await import(
  "../actions/list-image-set-versions.js"
);

describe("list-image-set-versions", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetID = "12345678901234567890123456789012";

    const response = {
      $metadata: {
        httpStatusCode: 200,
        requestId: "74590b37-a002-4827-83f2-3c590279c742",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      imageSetPropertiesList: [
        {
          ImageSetWorkflowStatus: "CREATED",
          createdAt: "2023-09-22T14:49:26.427Z",
          imageSetId: "xxxxxxxxxxxxxxxxxxxxxxx",
          imageSetState: "ACTIVE",
          versionId: "1",
        },
      ],
    };

    paginateListImageSetVersions.mockImplementationOnce(async function* () {
      yield response;
    });

    await listImageSetVersions(datastoreId, imageSetID);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
