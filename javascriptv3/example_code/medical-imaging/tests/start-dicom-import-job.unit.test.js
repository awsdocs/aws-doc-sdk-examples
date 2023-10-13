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

const { startDicomImportJob } = await import(
  "../actions/start-dicom-import-job.js"
);

describe("start-dicom-import-job", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";

    const response = {
      jobId: "12345678901234567890123456789012",
      datastoreId: datastoreId,
      jobStatus: "CREATING",
      submittedAt: "2019-01-01T00:00:00.000Z",
    };

    send.mockResolvedValueOnce(response);

    await startDicomImportJob(
      "test-1",
      datastoreId,
      "arn:aws:iam::xxxxxxxxxxxx:role/ImportJobDataAccessRole",
      "s3://medical-imaging-dicom-input/dicom_input/",
      "s3://medical-imaging-output/job_output/"
    );

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
