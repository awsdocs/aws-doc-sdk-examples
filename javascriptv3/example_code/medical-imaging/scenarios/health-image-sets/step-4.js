// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import fs from "node:fs/promises";
import { S3Client, GetObjectCommand } from "@aws-sdk/client-s3";

import {
  Scenario,
  ScenarioAction,
  ScenarioOutput,
} from "@aws-doc-sdk-examples/lib/scenario/index.js";

/**
 * @typedef {{ stackOutputs: {
 *   BucketName: string,
 *   DatastoreID: string,
 *   RoleArn: string
 * }, importJobId: string,
 * importJobOutputS3Uri: string,
 * imageSetIds: string[],
 * manifestContent: { jobSummary: { imageSetsSummary: { imageSetId: string }[] } }
 * }} State
 */

const loadState = new ScenarioAction("loadState", async (state) => {
  try {
    const stateFromDisk = JSON.parse(
      await fs.readFile("step-3-state.json", "utf8")
    );
    Object.assign(state, stateFromDisk);
  } catch (err) {
    console.error("Failed to load state from disk:", err);
  }
});

const s3Client = new S3Client({});

const getManifestFile = new ScenarioAction(
  "getManifestFile",
  async (/** @type {State} */ state) => {
    const bucket = state.stackOutputs.BucketName;
    const prefix = `output/${state.stackOutputs.DatastoreID}-DicomImport-${state.importJobId}/`;
    const key = `${prefix}job-output-manifest.json`;

    const command = new GetObjectCommand({
      Bucket: bucket,
      Key: key,
    });

    const response = await s3Client.send(command);
    const manifestContent = await response.Body.transformToString();
    state.manifestContent = JSON.parse(manifestContent);
  }
);

const parseManifestFile = new ScenarioAction(
  "parseManifestFile",
  (/** @type {State} */ state) => {
    const imageSetIds =
      state.manifestContent.jobSummary.imageSetsSummary.reduce(
        (imageSetIds, next) => {
          return { ...imageSetIds, [next.imageSetId]: next.imageSetId };
        },
        {}
      );
    state.imageSetIds = Object.keys(imageSetIds);
  }
);

const outputImageSetIds = new ScenarioOutput(
  "outputImageSetIds",
  (/** @type {State} */ state) =>
    `The image sets created by this import job are: \n${state.imageSetIds
      .map((id) => `Image set: ${id}`)
      .join("\n")}`
);

const saveState = new ScenarioAction("saveState", async (state) => {
  await fs.writeFile("step-4-state.json", JSON.stringify(state));
});

export const step4 = new Scenario(
  "Step 4: Get Image Set IDs",
  [loadState, getManifestFile, parseManifestFile, outputImageSetIds, saveState],
  {}
);
