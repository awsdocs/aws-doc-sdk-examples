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

const inputHandler = vi.fn();
vi.doMock("@aws-doc-sdk-examples/lib/scenario/index.js", async () => {
  const actual = await vi.importActual(
    "@aws-doc-sdk-examples/lib/scenario/index.js",
  );
  return {
    ...actual,
    ScenarioInput: vi.fn().mockImplementation(() => ({
      handle: inputHandler,
    })),
  };
});

const { Scenario } = await import(
  "@aws-doc-sdk-examples/lib/scenario/index.js"
);

const medicalImagingClientSendMock = vi.fn();
vi.doMock("@aws-sdk/client-medical-imaging", async () => {
  const actual = await vi.importActual("@aws-sdk/client-medical-imaging");
  return {
    ...actual,
    DeleteImageSetCommand: vi.fn().mockImplementation((input) => input),
    MedicalImagingClient: vi.fn().mockImplementation(() => ({
      send: medicalImagingClientSendMock,
    })),
  };
});

const cloudFormationClientSendMock = vi.fn();
vi.doMock("@aws-sdk/client-cloudformation", async () => {
  const actual = await vi.importActual("@aws-sdk/client-cloudformation");
  return {
    ...actual,
    DeleteStackCommand: vi.fn().mockImplementation((input) => input),
    CloudFormationClient: vi.fn().mockImplementation(() => ({
      send: cloudFormationClientSendMock,
    })),
  };
});

const { confirmCleanup, deleteImageSets, deleteStack } = await import(
  "../scenarios/health-image-sets/clean-up-steps.js"
);

describe("clean-up-steps", () => {
  const mockState = {
    getStackName: "test-stack",
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
          Series: [],
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
          Series: [],
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
          Series: [],
        },
      },
    ],
  };

  const cleanUpSteps = new Scenario(
    "clean-up-steps",
    [confirmCleanup, deleteImageSets, deleteStack],
    mockState,
  );

  beforeEach(() => {
    vi.clearAllMocks();

    readFileMock.mockResolvedValue(JSON.stringify(mockState));
  });

  it("should delete image sets and CloudFormation stack when user confirms", async () => {
    inputHandler.mockImplementationOnce((/** @type {object} */ state) => {
      state.confirmCleanup = true;
    });

    medicalImagingClientSendMock
      .mockResolvedValueOnce({}) // For image-set-1
      .mockResolvedValueOnce({}) // For image-set-2
      .mockRejectedValueOnce({
        name: "ConflictException",
      }); // Simulate image set already deleted

    await cleanUpSteps.run({ confirmAll: true, verbose: false });

    expect(medicalImagingClientSendMock).toHaveBeenCalledTimes(3);
    expect(medicalImagingClientSendMock).toHaveBeenCalledWith({
      datastoreId: "datastore-123",
      imageSetId: "image-set-1",
    });
    expect(medicalImagingClientSendMock).toHaveBeenCalledWith({
      datastoreId: "datastore-123",
      imageSetId: "image-set-2",
    });

    expect(cloudFormationClientSendMock).toHaveBeenCalledTimes(1);
    expect(cloudFormationClientSendMock).toHaveBeenCalledWith({
      StackName: "test-stack",
    });
  });

  it("should skip cleanup when user does not confirm", async () => {
    inputHandler.mockImplementationOnce((/** @type {object} */ state) => {
      state.confirmCleanup = false;
    });

    await cleanUpSteps.run({ confirmAll: true, verbose: false });

    expect(medicalImagingClientSendMock).toHaveBeenCalledTimes(0);
    expect(cloudFormationClientSendMock).toHaveBeenCalledTimes(0);
  });
});
