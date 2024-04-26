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

const stateFilePath = "step-4-state.json";

const s3ClientSendMock = vi.fn();
vi.doMock("@aws-sdk/client-s3", async () => {
  const actual = await vi.importActual("@aws-sdk/client-s3");
  return {
    ...actual,
    S3Client: vi.fn().mockImplementation(() => ({
      send: s3ClientSendMock,
    })),
  };
});

const { step4 } = await import("../scenarios/health-image-sets/step-4.js");

describe("step4", () => {
  const mockState = {
    stackOutputs: {
      BucketName: "input-bucket",
      DatastoreID: "datastore-123",
      RoleArn: "arn:aws:iam::123456789012:role/test-role",
    },
    importJobId: "import-job-123",
    importJobOutputS3Uri: "s3://output-bucket/output-prefix/",
  };

  beforeEach(() => {
    vi.clearAllMocks();

    readFileMock.mockResolvedValueOnce(JSON.stringify(mockState));
  });

  it("should set imageSetIds correctly after parsing job-output-manifest.json", async () => {
    const manifestContent = {
      jobSummary: {
        imageSetsSummary: [
          { imageSetId: "image-set-1" },
          { imageSetId: "image-set-2" },
          { imageSetId: "image-set-3" },
        ],
      },
    };

    s3ClientSendMock.mockResolvedValueOnce({
      Body: { transformToString: () => JSON.stringify(manifestContent) },
    });

    await step4.run({ confirmAll: true, verbose: false });

    expect(writeFileMock).toHaveBeenCalledWith(
      stateFilePath,
      JSON.stringify({
        name: step4.name,
        ...mockState,
        manifestContent,
        imageSetIds: ["image-set-1", "image-set-2", "image-set-3"],
      }),
    );
  });
});
