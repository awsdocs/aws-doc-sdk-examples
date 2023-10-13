/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const send = vi.fn();

vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");
  return {
    ...actual,
    MedicalImagingClient: class {
      send = send;
    },
  };
});

const { deleteImageSet } = await import("../actions/delete-image-set.js");

describe("delete-image-set", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetID = "12345678901234567890123456789012";

    const response = {
      $metadata: {
        httpStatusCode: 200,
        requestId: "6267bbd2-eaa5-4a50-8ee8-8fddf535cf73",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      datastoreId: "xxxxxxxxxxxxxxxx",
      imageSetId: "xxxxxxxxxxxxxxx",
      imageSetState: "LOCKED",
      imageSetWorkflowStatus: "DELETING",
    };

    send.mockResolvedValueOnce(response);

    await deleteImageSet(datastoreId, imageSetID);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
