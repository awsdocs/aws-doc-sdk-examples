/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/* This example code showcases how Amazon S3 can be used as a core component of an application.
 * We'll walk through how to:
 * - Create a bucket
 * - Upload files to the bucket
 * - List files in the bucket
 * - Copy files from another bucket to this one
 * - Download files from the bucket
 * - Empty the bucket
 * - Delete the bucket
 */

// snippet-start:[javascript.v3.s3.scenarios.basic.imports]
// Used to check if currently running file is this file.
import { fileURLToPath } from "url";
import { readdirSync, readFileSync } from "fs";
import { createInterface } from "readline";

// A local helper utility.
import { dirnameFromMetaUrl } from "libs/utils/util-fs.js";

import {
  S3Client,
  CreateBucketCommand,
  PutObjectCommand,
  ListObjectsCommand,
  CopyObjectCommand,
  GetObjectCommand,
  DeleteObjectsCommand,
  DeleteBucketCommand,
} from "@aws-sdk/client-s3";
// snippet-end:[javascript.v3.s3.scenarios.basic.imports]

// snippet-start[javascript.v3.s3.scenarios.basic.S3Client]
// The region can be provided as an argument to S3Client or
// declared in the AWS configuration file. In this case
// we're using the region provided in the AWS configuration.
const s3Client = new S3Client({});
// snippet-end[javascript.v3.s3.scenarios.basic.S3Client]

// snippet-start[javascript.v3.s3.scenarios.basic.CreateBucket]
export const createBucket = async ({ bucketName }) => {
  const command = new CreateBucketCommand({ Bucket: bucketName });
  await s3Client.send(command);
  console.log("Bucket created successfully.");
};
// snippet-end[javascript.v3.s3.scenarios.basic.CreateBucket]

// snippet-start[javascript.v3.s3.scenarios.basic.PutObject]
export const uploadFilesToBucket = async ({ bucketName, folderPath }) => {
  console.log(`Uploading files from ${folderPath}`);
  const keys = readdirSync(folderPath);
  const files = keys.map((key) => {
    const filePath = `${folderPath}/${key}`;
    const fileContent = readFileSync(filePath);
    return {
      Key: key,
      Body: fileContent,
    };
  });

  for (let file of files) {
    await s3Client.send(
      new PutObjectCommand({
        Bucket: bucketName,
        Body: file.Body,
        Key: file.Key,
      })
    );
    console.log(`${file.Key} uploaded successfully.`);
  }
};
// snippet-end[javascript.v3.s3.scenarios.basic.PutObject]

const listFilesInBucket = async ({ bucketName }) => {};

const copyFileFromBucket = async ({
  sourceBucket,
  sourceKey,
  destinationBucket,
  destinationKey,
}) => {};

const downloadFilesFromBucket = async ({ keys }) => {};

const emptyBucket = async ({ bucketName }) => {};

const deleteBucket = async ({ bucketName }) => {};

// snippet-start:[javascript.v3.s3.scenarios.basic.main]
const main = async () => {
  const BUCKET_NAME = "my-bucket-corey";
  const OBJECT_DIRECTORY = `${dirnameFromMetaUrl(
    import.meta.url
  )}../../../../resources/sample_files/.sample_media`;

  try {
    await createBucket({ bucketName: BUCKET_NAME });
    await uploadFilesToBucket({
      bucketName: BUCKET_NAME,
      folderPath: OBJECT_DIRECTORY,
    });
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[javascript.v3.s3.scenarios.basic.main]

// snippet-start:[javascript.v3.s3.scenarios.basic.runner]
// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
// snippet-end:[javascript.v3.s3.scenarios.basic.runner]
