// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  ListObjectVersionsCommand,
  GetObjectCommand,
  CopyObjectCommand,
  PutObjectCommand,
} from "@aws-sdk/client-s3";

import * as data from "./object_name.json" assert { type: "json" };
import { readFile } from "node:fs/promises";

/**
 * @typedef {import("@aws-doc-sdk-examples/lib/scenario/index.js")} Scenarios
 */

/**
 * @typedef {import("@aws-sdk/client-s3").S3Client} S3Client
 */

const choices = {
  EXIT: 0,
  LIST_ALL_FILES: 1,
  CONDITIONAL_READ: 2,
  CONDITIONAL_COPY: 3,
  CONDITIONAL_WRITE: 4,
};

//const delay = (ms) => new Promise((res) => setTimeout(res, ms));

/**
 * @param {Scenarios} scenarios
 */
const replInput = (scenarios) =>
  new scenarios.ScenarioInput(
    "replChoice",
    "Explore the S3 locking features by selecting one of the following choices",
    {
      type: "select",
      choices: [
        { name: "Print list of bucket items.", value: choices.LIST_ALL_FILES },
        {
          name: "Perform a conditional read.",
          value: choices.CONDITIONAL_READ,
        },
        {
          name: "Perform a conditional copy.",
          value: choices.CONDITIONAL_COPY,
        },
        {
          name: "Perform a conditional write.",
          value: choices.CONDITIONAL_WRITE,
        },
        { name: "Clean up and exit scenario.", value: choices.EXIT },
      ],
    }
  );

/**
 * @param {S3Client} client
 * @param {string[]} buckets
 */
const getAllFiles = async (client, buckets) => {
  /** @type {{bucket: string, key: string, version: string}[]} */

  const files = [];
  for (const bucket of buckets) {
    const objectsResponse = await client.send(
      new ListObjectVersionsCommand({ Bucket: bucket })
    );
    for (const version of objectsResponse.Versions || []) {
      const { Key } = version;
      files.push({ bucket, key: Key });
    }
  }
  return files;
};

/**
 * @param {S3Client} client
 * @param {string[]} buckets
 */
const getEtag = async (client, bucket, key) => {
  const objectsResponse = await client.send(
    new GetObjectCommand({
      Bucket: bucket,
      Key: key,
    })
  );
  return objectsResponse.ETag;
};

/**
 * @param {S3Client} client
 * @param {string[]} buckets
 */

/**
 * @param {Scenarios} scenarios
 * @param {S3Client} client
 */
const replAction = (scenarios, client) =>
  new scenarios.ScenarioAction(
    "replAction",
    async (state) => {
      const files = await getAllFiles(client, [
        state.sourceBucketName,
        state.destinationBucketName,
      ]);

      const fileInput = new scenarios.ScenarioInput(
        "selectedFile",
        "Select a file to use:",
        {
          type: "select",
          choices: files.map((file, index) => ({
            name: `${index + 1}: ${file.bucket}: ${file.key} (Etag: ${
              file.version
            })`,
            value: index,
          })),
        }
      );
      const condReadOptions = new scenarios.ScenarioInput(
        "selectOption",
        "Which conditional read action would you like to take?",
        {
          type: "select",
          choices: [
            "If-Match: using the object's ETag. This condition should succeed.",
            "If-None-Match: using the object's ETag. This condition should fail.",
            "If-Modified-Since: using yesterday's date. This condition should succeed.",
            "If-Unmodified-Since: using yesterday's date. This condition should fail.",
          ],
        }
      );
      const condCopyOptions = new scenarios.ScenarioInput(
        "selectOption",
        "Which conditional copy action would you like to take?",
        {
          type: "select",
          choices: [
            "If-Match: using the object's ETag. This condition should succeed.",
            "If-None-Match: using the object's ETag. This condition should fail.",
            "If-Modified-Since: using yesterday's date. This condition should succeed.",
            "If-Unmodified-Since: using yesterday's date. This condition should fail.",
          ],
        }
      );
      const condWriteOptions = new scenarios.ScenarioInput(
        "selectOption",
        "Which conditional write action would you like to take?",
        {
          type: "select",
          choices: [
            "IfNoneMatch condition on the object key: If the key is a duplicate, the write will fail.",
          ],
        }
      );

      const { replChoice } = state;

      switch (replChoice) {
        case choices.LIST_ALL_FILES: {
          const files = await getAllFiles(client, [
            state.sourceBucketName,
            state.destinationBucketName,
          ]);
          state.replOutput =
            "Listing the objects and buckets. \n" +
            files
              .map(
                (file) =>
                  `Items in bucket ${file.bucket}:\n object: ${file.key} `
              )
              .join("\n");
          break;
        }
        case choices.CONDITIONAL_READ: {
          /** @type {number} */

          //Get yesterday's date.
          var date = new Date();
          date.setDate(date.getDate() - 1);

          const selectedCondRead = await condReadOptions.handle(state);
          if (
            selectedCondRead ==
            "If-Match: using the object's ETag. This condition should succeed."
          ) {
            //Get ETag of selected file.
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            const ETag = await getEtag(client, bucket, key);

            try {
              await client.send(
                new GetObjectCommand({
                  Bucket: bucket,
                  Key: key,
                  IfMatch: ETag,
                })
              );
              state.replOutput = ` file0.txt in bucket ${state.sourceBucketName} returned because ETag provided matches the object's ETag.`;
            } catch (err) {
              state.replOutput = `Unable to return object file0.txt in bucket ${state.sourceBucketName}: ${err.message}`;
            }
            break;
          }
          if (
            selectedCondRead ==
            "If-None-Match: using the object's ETag. This condition should fail."
          ) {
            //Get ETag of selected file.
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            const ETag = await getEtag(client, bucket, key);

            try {
              await client.send(
                new GetObjectCommand({
                  Bucket: bucket,
                  Key: key,
                  IfNoneMatch: ETag,
                })
              );
              state.replOutput = `file0.txt in ${state.sourceBucketName} was returned.`;
            } catch (err) {
              state.replOutput = `file0.txt in ${state.sourceBucketName} was not returned because ETag provided matches the object's ETag. : ${err.message}`;
            }
            break;
          }
          if (
            selectedCondRead ==
            "If-Modified-Since: using yesterday's date. This condition should succeed."
          ) {
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            try {
              await client.send(
                new GetObjectCommand({
                  Bucket: bucket,
                  Key: key,
                  IfModifiedSince: date,
                })
              );
              state.replOutput = `file0.txt in bucket ${state.sourceBucketName} returned because it has been created or modified in the last 24 hours.`;
            } catch (err) {
              state.replOutput = `Unable to return object file0.txt in bucket ${state.sourceBucketName}: ${err.message}`;
            }
            break;
          }
          if (
            selectedCondRead ==
            "If-Unmodified-Since: using yesterday's date. This condition should fail."
          ) {
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            try {
              await client.send(
                new GetObjectCommand({
                  Bucket: bucket,
                  Key: key,
                  IfUnmodifiedSince: date,
                })
              );
              state.replOutput = `file0.txt in ${state.sourceBucketName} was returned.`;
            } catch (err) {
              state.replOutput = `file0.txt in ${state.sourceBucketName} was not returned because it was created or modified in the last 24 hours. : ${err.message}`;
            }
            break;
          }
        }

        case choices.CONDITIONAL_COPY: {
          //Get yesterday's date.
          var date = new Date();
          date.setDate(date.getDate() - 1);

          const selectedCondCopy = await condCopyOptions.handle(state);
          if (
            selectedCondCopy ==
            "If-Match: using the object's ETag. This condition should succeed."
          ) {
            //Get ETag of selected file.
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            const ETag = await getEtag(client, bucket, key);

            const copySource = bucket + "/" + key;
            const name = data.default.name;
            const copiedKey = name + key;
            try {
              await client.send(
                new CopyObjectCommand({
                  CopySource: copySource,
                  Bucket: state.destinationBucketName,
                  Key: copiedKey,
                  IfMatch: ETag,
                })
              );
              state.replOutput =
                copiedKey +
                " copied to bucket " +
                state.destinationBucketName +
                " because ETag provided matches the object's ETag.";
            } catch (err) {
              state.replOutput =
                "Unable to copy object text01.txt to bucket " +
                state.destinationBucketName +
                ":" +
                err.message;
            }
            break;
          }
          if (
            selectedCondCopy ==
            "If-None-Match: using the object's ETag. This condition should fail."
          ) {
            //Get ETag of selected file.
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            const ETag = await getEtag(client, bucket, key);
            const copySource = bucket + "/" + key;
            const copiedKey = "test-111-file0.txt";

            try {
              await client.send(
                new CopyObjectCommand({
                  CopySource: copySource,
                  Bucket: state.destinationBucketName,
                  Key: copiedKey,
                  IfNoneMatch: ETag,
                })
              );
              state.replOutput =
                copiedKey + " copied to bucket " + state.destinationBucketName;
            } catch (err) {
              state.replOutput =
                "Unable to copy object text01.txt to bucket " +
                state.destinationBucketName +
                " because ETag provided matches the object's ETag." +
                ":" +
                err.message;
            }
            break;
          }
          if (
            selectedCondCopy ==
            "If-Modified-Since: using yesterday's date. This condition should succeed."
          ) {
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            const copySource = bucket + "/" + key;
            const copiedKey = "test-111-file0.txt";

            try {
              await client.send(
                new CopyObjectCommand({
                  CopySource: copySource,
                  Bucket: state.destinationBucketName,
                  Key: copiedKey,
                  IsModifiedSince: date,
                })
              );
              state.replOutput =
                copiedKey +
                " copied to bucket " +
                state.destinationBucketName +
                "because it has been created or modified in the last 24 hours.";
            } catch (err) {
              state.replOutput =
                "Unable to copy object text01.txt to bucket " +
                state.destinationBucketName +
                ":" +
                err.message;
            }
            break;
          }
          if (
            selectedCondCopy ==
            "If-Unmodified-Since: using yesterday's date. This condition should fail."
          ) {
            const bucket = state.sourceBucketName;
            const key = "file0.txt";
            const copySource = bucket + "/" + key;
            const copiedKey = "test-111-file0.txt";

            try {
              await client.send(
                new CopyObjectCommand({
                  CopySource: copySource,
                  Bucket: state.destinationBucketName,
                  Key: copiedKey,
                  IsUnmodifiedSince: date,
                })
              );
              state.replOutput =
                "Unable to copy object text01.txt to bucket " +
                state.destinationBucketName +
                ". Precondition not met.";
            } catch (err) {
              state.replOutput =
                copiedKey +
                " copied to bucket " +
                state.destinationBucketName +
                "because it has been created or modified in the last 24 hours." +
                ":" +
                err.message;
            }
          }
          break;
        }
        case choices.CONDITIONAL_WRITE: {
          //Get yesterday's date.
          var date = new Date();
          date.setDate(date.getDate() - 1);

          const selectedCondWrite = await condWriteOptions.handle(state);
          if (
            selectedCondWrite ==
            "IfNoneMatch condition on the object key: If the key is a duplicate, the write will fail."
          ) {
            const filePath = "./text02.txt";
            try {
              await client.send(
                new PutObjectCommand({
                  Bucket: state.destinationBucketName,
                  Key: "text02.txt",
                  Body: await readFile(filePath),
                  IfNoneMatch: "*",
                })
              );
              state.replOutput =
                " copied to bucket " +
                state.destinationBucketName +
                " because the key is not a duplicate.";
            } catch (err) {
              state.replOutput =
                "Unable to copy object " +
                " to bucket " +
                state.destinationBucketName +
                ":" +
                err.message;
            }
            break;
          }
        }
        default:
          throw new Error(`Invalid replChoice: ${replChoice}`);
      }
    },
    {
      whileConfig: {
        whileFn: ({ replChoice }) => replChoice !== choices.EXIT,
        input: replInput(scenarios),
        output: new scenarios.ScenarioOutput(
          "REPL output",
          (state) => state.replOutput,
          { preformatted: true }
        ),
      },
    }
  );

export { replInput, replAction, choices };
