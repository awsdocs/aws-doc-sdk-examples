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
  "@aws-doc-sdk-examples/lib/scenario/scenario.js"
);

const s3Client = vi.fn();
const listObjectsV2Command = vi.fn();
const copyObjectCommand = vi.fn();
const s3Constructor = vi.fn().mockReturnValue({ send: s3Client });
vi.doMock("@aws-sdk/client-s3", async () => {
  const actual = await vi.importActual("@aws-sdk/client-s3");
  return {
    ...actual,
    S3Client: s3Constructor,
    ListObjectsV2Command: listObjectsV2Command,
    CopyObjectCommand: copyObjectCommand,
  };
});

const { doCopy, selectDataset, copyDataset, outputCopiedObjects } =
  await import("../scenarios/health-image-sets/dataset-steps.js");

describe("dataset-steps", () => {
  const datasetSteps = new Scenario("dataset-steps", [
    doCopy,
    selectDataset,
    copyDataset,
    outputCopiedObjects,
  ]);
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should copy the selected dataset from the source bucket to the target bucket", async () => {
    const sourceBucket = "idc-open-data";
    const sourcePrefix = "0002d261-8a5d-4e63-8e2e-0cbfac87b904";
    const targetBucket = "my-bucket";
    const targetPrefix = "input/";
    const objectKeys = ["a.dcm", "b.dcm", "c.dcm"];

    inputHandler
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.doCopy = true;
      })
      .mockImplementationOnce((/** @type {object} */ state) => {
        state.selectDataset = sourcePrefix;
        state.stackOutputs = { BucketName: targetBucket };
      });

    s3Client
      .mockResolvedValueOnce({
        Contents: objectKeys.map((key) => ({ Key: `${sourcePrefix}/${key}` })),
      })
      .mockResolvedValueOnce(() => ({
        CopyObjectResult: {},
      }));

    await datasetSteps.run({ confirmAll: true, verbose: false });

    expect(s3Client).toHaveBeenCalledTimes(objectKeys.length + 1);
    expect(listObjectsV2Command).toHaveBeenCalledWith({
      Bucket: sourceBucket,
      Prefix: sourcePrefix,
    });

    for (const key of objectKeys) {
      expect(copyObjectCommand).toHaveBeenCalledWith({
        Bucket: targetBucket,
        CopySource: `/${sourceBucket}/${sourcePrefix}/${key}`,
        Key: `${targetPrefix}${key}`,
      });
    }
  });
});
