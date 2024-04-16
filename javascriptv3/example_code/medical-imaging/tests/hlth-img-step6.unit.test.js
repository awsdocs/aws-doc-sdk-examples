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

const mockChild = {
  on: vi.fn(),
  stdout: { on: vi.fn() },
  stderr: { on: vi.fn() },
};
const spawn = vi.fn().mockReturnValue(mockChild);
vi.doMock("node:child_process", () => {
  return {
    spawn,
  };
});

const { step6 } = await import("../scenarios/health-image-sets/step-6.js");

describe("step6", () => {
  const mockState = {
    stackOutputs: {
      BucketName: "input-bucket",
      DatastoreID: "datastore-123",
      RoleArn: "arn:aws:iam::123456789012:role/test-role",
    },
    imageSetMetadata: [
      {
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
          Series: {
            "1.2.3.4": {
              DICOM: {
                /* Series metadata */
              },
              Instances: {
                "1.2.3.4.5": {
                  DICOM: {
                    SOPInstanceId: "1.2.3.4.5",
                  },
                  DICOMVRs: {},
                  ImageFrames: [],
                },
              },
            },
          },
        },
      },
      {
        SchemaVersion: "1.0",
        DatastoreID: "datastore-123",
        ImageSetID: "image-set-2",
        Patient: {
          DICOM: {
            /* Patient metadata */
          },
        },
        Study: {
          DICOM: {
            /* Study metadata */
          },
          Series: {
            "5.6.7.8": {
              DICOM: {
                /* Series metadata */
              },
              Instances: {
                "5.6.7.8.9": {
                  DICOM: {
                    SOPInstanceId: "5.6.7.8.9",
                  },
                  DICOMVRs: {},
                  ImageFrames: [],
                },
                "5.6.7.8.10": {
                  DICOM: {
                    SOPInstanceId: "5.6.7.8.10",
                  },
                  DICOMVRs: {},
                  ImageFrames: [],
                },
              },
            },
          },
        },
      },
    ],
  };

  beforeEach(() => {
    vi.clearAllMocks();

    readFileMock.mockResolvedValue(JSON.stringify(mockState));
  });

  it("should spawn the verification tool with correct arguments", async () => {
    mockChild.on.mockImplementation((event, callback) => {
      if (event === "exit") {
        callback(0);
      }
    });

    await step6.run({ confirmAll: true, verbose: false });

    expect(spawn).toHaveBeenCalledTimes(3);
    expect(spawn).toHaveBeenCalledWith(
      "node",
      expect.arrayContaining([
        "./pixel-data-verification/index.js",
        "datastore-123",
        "image-set-1",
        "1.2.3.4",
        "1.2.3.4.5",
      ]),
      { stdio: "inherit" },
    );
    expect(spawn).toHaveBeenCalledWith(
      "node",
      expect.arrayContaining([
        "./pixel-data-verification/index.js",
        "datastore-123",
        "image-set-2",
        "5.6.7.8",
        "5.6.7.8.9",
      ]),
      { stdio: "inherit" },
    );
    expect(spawn).toHaveBeenCalledWith(
      "node",
      expect.arrayContaining([
        "./pixel-data-verification/index.js",
        "datastore-123",
        "image-set-2",
        "5.6.7.8",
        "5.6.7.8.10",
      ]),
      { stdio: "inherit" },
    );
  });
});
