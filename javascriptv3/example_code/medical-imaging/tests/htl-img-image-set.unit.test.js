// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { expect, it, vi, describe, beforeEach } from "vitest";
import { Scenario } from "@aws-doc-sdk-examples/lib/scenario/scenario.js";

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

const { getManifestFile, parseManifestFile, outputImageSetIds } = await import(
  "../scenarios/health-image-sets/image-set-steps.js"
);

describe("image-set-steps", () => {
  const mockState = {
    earlyExit: false,
    stackOutputs: {
      BucketName: "input-bucket",
      DatastoreID: "datastore-123",
      RoleArn: "arn:aws:iam::123456789012:role/test-role",
    },
    importJobId: "import-job-123",
    importJobOutputS3Uri: "s3://output-bucket/output-prefix/",
  };

  const imageSetSteps = new Scenario(
    "image-set-steps",
    [getManifestFile, parseManifestFile, outputImageSetIds],
    mockState,
  );

  beforeEach(() => {
    vi.clearAllMocks();
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

    await imageSetSteps.run({ confirmAll: true, verbose: false });

    expect(imageSetSteps.state).toEqual({
      name: imageSetSteps.name,
      ...mockState,
      manifestContent,
      imageSetIds: ["image-set-1", "image-set-2", "image-set-3"],
    });
  });
});
