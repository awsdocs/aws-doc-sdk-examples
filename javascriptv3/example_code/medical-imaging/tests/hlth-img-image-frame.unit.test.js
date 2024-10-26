// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { expect, it, vi, describe, beforeEach } from "vitest";
import { gzip } from "node:zlib";
import { promisify } from "node:util";
import { Scenario } from "@aws-doc-sdk-examples/lib/scenario/scenario.js";

const gzipAsync = promisify(gzip);

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

const { getImageSetMetadata, outputImageFrameIds } = await import(
  "../scenarios/health-image-sets/image-frame-steps.js"
);

describe("image-frame-steps", () => {
  const mockState = {
    earlyExit: false,
    stackOutputs: {
      BucketName: "input-bucket",
      DatastoreID: "datastore-123",
      RoleArn: "arn:aws:iam::123456789012:role/test-role",
    },
    imageSetIds: ["image-set-1", "image-set-2"],
  };

  const imageFrameSteps = new Scenario(
    "image-frame-steps",
    [getImageSetMetadata, outputImageFrameIds],
    mockState,
  );

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should handle the GetImageSetMetadataCommand response correctly", async () => {
    const mockMetadata = {
      SchemaVersion: "1.0",
      DatastoreID: "datastore-123",
      ImageSetID: "image-set-1",
      Patient: {
        DICOM: {
          /* Patient metadata */
        },
      },
      Study: {
        DICOM: {
          /* Study metadata */
        },
        Series: [
          {
            DICOM: {
              /* Series metadata */
            },
            Instances: {
              /* Instance metadata */
            },
          },
        ],
      },
    };

    const compressedMetadata = await gzipAsync(JSON.stringify(mockMetadata));

    medicalImagingClientSendMock
      .mockResolvedValueOnce({
        imageSetMetadataBlob: {
          transformToByteArray: () => compressedMetadata,
        },
      })
      .mockResolvedValueOnce({
        imageSetMetadataBlob: {
          transformToByteArray: () => compressedMetadata,
        },
      });

    await imageFrameSteps.run({ confirmAll: true, verbose: false });

    expect(imageFrameSteps.state).toEqual({
      name: imageFrameSteps.name,
      ...mockState,
      imageSetMetadata: [mockMetadata, mockMetadata],
    });
  });
});
