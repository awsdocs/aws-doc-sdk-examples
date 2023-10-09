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

const { updateImageSetMetadata } = await import(
  "../actions/update-image-set-metadata.js"
);

describe("update-image-set-metadata", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";
    const imageSetId = "12345678901234567890123456789012";
    const versionID = "1";
    const updatableAttributes = JSON.stringify({
      SchemaVersion: 1.1,
      Patient: {
        DICOM: {
          PatientName: "Garcia^Gloria",
        },
      },
    });

    const updateMetadata = {
      DICOMUpdates: {
        updatableAttributes: new TextEncoder().encode(updatableAttributes),
      },
    };
    const response = {
      $metadata: {
        httpStatusCode: 200,
        requestId: "7966e869-e311-4bff-92ec-56a61d3003ea",
        extendedRequestId: undefined,
        cfId: undefined,
        attempts: 1,
        totalRetryDelay: 0,
      },
      createdAt: "2023-09-22T14:49:26.427Z",
      datastoreId: "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      imageSetId: "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
      imageSetState: "LOCKED",
      imageSetWorkflowStatus: "UPDATING",
      latestVersionId: "4",
      updatedAt: "2023-09-27T19:41:43.494Z",
    };

    send.mockResolvedValueOnce(response);

    await updateImageSetMetadata(
      datastoreId,
      imageSetId,
      versionID,
      updateMetadata
    );

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
