/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { describe, it, expect, vi } from "vitest";

const paginateListDICOMImportJobs = vi.fn();

vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");

  return {
    ...actual,
    paginateListDICOMImportJobs,
  };
});

const { listDICOMImportJobs } = await import(
  "../actions/list-dicom-import-jobs.js"
);

describe("list-dicom-import-job", () => {
  it("should log the response", async () => {
    const logSpy = vi.spyOn(console, "log");
    const datastoreId = "12345678901234567890123456789012";

    const response = {
      jobSummaries: [
        {
          dataAccessRoleArn: "arn:aws:iam::xxxxxxxxxxxx:role/dicom_import",
          datastoreId: datastoreId,
          endedAt: "2023-09-22T14:49:51.351Z",
          jobId: "12345678901234567890123456789012",
          jobName: "test-1",
          jobStatus: "COMPLETED",
          submittedAt: "2023-09-22T14:48:45.767Z",
        },
      ],
    };

    paginateListDICOMImportJobs.mockImplementationOnce(async function* () {
      yield response;
    });

    await listDICOMImportJobs(datastoreId);

    expect(logSpy).toHaveBeenCalledWith(response);
  });
});
