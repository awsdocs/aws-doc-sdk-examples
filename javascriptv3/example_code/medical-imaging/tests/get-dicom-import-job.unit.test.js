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

const { getDICOMImportJob } = await import(
  "../actions/get-dicom-import-job.js"
);

describe("get-dicom-import-job", () => {
  const jobId = "12345678901234567890123456789012";
  const datastoreId = "12345678901234567890123456789012";
  it("Should accept arguments and response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const response = {
      jobId: jobId,
      datastoreId: datastoreId,
      jobStatus: "TESTING",
      submittedAt: "2019-01-01T00:00:00.000Z",
    };

    send.mockResolvedValueOnce(response);

    await getDICOMImportJob(datastoreId, jobId);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
