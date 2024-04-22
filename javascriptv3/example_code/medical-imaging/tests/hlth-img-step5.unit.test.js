// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { expect, it, vi, describe, beforeEach } from "vitest";
import { gzip } from "zlib";
import { promisify } from "util";

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

const gzipAsync = promisify(gzip);

const stateFilePath = "step-5-state.json";

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

const { step5 } = await import("../scenarios/health-image-sets/step-5.js");

describe("step5", () => {
  const mockState = {
    stackOutputs: {
      BucketName: "input-bucket",
      DatastoreID: "datastore-123",
      RoleArn: "arn:aws:iam::123456789012:role/test-role",
    },
    imageSetIds: ["image-set-1", "image-set-2"],
  };

  beforeEach(() => {
    vi.clearAllMocks();

    readFileMock.mockResolvedValue(JSON.stringify(mockState));
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

    await step5.run({ confirmAll: true, verbose: false });

    expect(writeFileMock).toHaveBeenCalledWith(
      stateFilePath,
      JSON.stringify({
        name: step5.name,
        ...mockState,
        imageSetMetadata: [mockMetadata, mockMetadata],
      }),
    );
  });
});
