// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { expect, it, vi, describe, beforeEach } from "vitest";
import { Scenario } from "@aws-doc-sdk-examples/lib/scenario/scenario.js";

const medicalImagingClientSendMock = vi.fn();
vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");
  return {
    ...actual,
    MedicalImagingClient: vi.fn().mockImplementation(() => ({
      send: medicalImagingClientSendMock,
    })),
  };
});

const retryMock = vi.fn().mockImplementation((_, fn) => fn());

vi.doMock("@aws-doc-sdk-examples/lib/utils/util-timers.js", () => ({
  retry: retryMock,
}));

const {
  doImport,
  startDICOMImport,
  waitForImportJobCompletion,
  outputImportJobStatus,
} = await import("../scenarios/health-image-sets/import-steps.js");

describe("importSteps", () => {
  const mockState = {
    name: "import-steps",
    earlyExit: false,
    stackOutputs: {
      BucketName: "input-bucket",
      DatastoreID: "datastore-123",
      RoleArn: "arn:aws:iam::123456789012:role/test-role",
    },
    importJobId: "import-job-123",
    importJobOutputS3Uri: "s3://output-bucket/output-prefix/",
    doImport: true,
  };

  const importSteps = new Scenario(
    "import-steps",
    [
      doImport,
      startDICOMImport,
      waitForImportJobCompletion,
      outputImportJobStatus,
    ],
    mockState,
  );

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should store importJobOutputS3Uri in state when import job is completed", async () => {
    medicalImagingClientSendMock.mockResolvedValue({
      jobId: "import-job-123",
      jobProperties: {
        jobStatus: "COMPLETED",
        outputS3Uri: "s3://output-bucket/output-prefix/",
      },
    });

    await importSteps.run({ confirmAll: true, verbose: false });

    expect(importSteps.state).toEqual(mockState);
  });
});
