// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { expect, it, vi, describe, beforeEach } from "vitest";

const writeFileMock = vi.fn();
const readFileMock = vi.fn();
const fsMod = {
  writeFile: writeFileMock,
  readFile: readFileMock,
};
vi.doMock("node:fs/promises", () => ({
  default: fsMod,
  ...fsMod,
}));

const stateFilePath = "step-3-state.json";

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

const { step3 } = await import("../scenarios/health-image-sets/step-3.js");

describe("step3", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should store importJobOutputS3Uri in state when import job is completed", async () => {
    const mockState = {
      name: step3.name,
      stackOutputs: {
        BucketName: "input-bucket",
        DatastoreID: "datastore-123",
        RoleArn: "arn:aws:iam::123456789012:role/test-role",
      },
      doImport: true,
    };

    readFileMock.mockResolvedValueOnce(JSON.stringify(mockState));

    medicalImagingClientSendMock.mockResolvedValue({
      jobId: "import-job-123",
      jobProperties: {
        jobStatus: "COMPLETED",
        outputS3Uri: "s3://output-bucket/output-prefix/",
      },
    });

    await step3.run({ confirmAll: true, verbose: false });

    expect(writeFileMock).toHaveBeenCalledWith(
      stateFilePath,
      JSON.stringify({
        ...mockState,
        importJobId: "import-job-123",
        importJobOutputS3Uri: "s3://output-bucket/output-prefix/",
      }),
    );
  });
});
