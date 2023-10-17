/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/* This example code shows you how to use Amazon S3 can be used as a core component of an application.
 * You'll do the following:
 * - Create a bucket.
 * - Upload files to the bucket.
 * - List files in the bucket.
 * - Copy files from another bucket to this one.
 * - Download files from the bucket.
 * - Empty the bucket.
 * - Delete the bucket.
 */

// snippet-start:[javascript.v3.s3.scenarios.basic.imports]
// Used to check if currently running file is this file.
import { fileURLToPath } from "url";
import { readdirSync, readFileSync, writeFileSync } from "fs";

// Local helper utils.
import { dirnameFromMetaUrl } from "@aws-sdk-examples/libs/utils/util-fs.js";
import {
  promptForText,
  promptToContinue,
} from "@aws-sdk-examples/libs/utils/util-io.js";
import { wrapText } from "@aws-sdk-examples/libs/utils/util-string.js";

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

// snippet-start:[javascript.v3.s3.scenarios.basic.S3Client]
// The Region can be provided as an argument to S3Client or
// declared in the AWS configuration file. In this case
// we're using the Region provided in the AWS configuration.
const s3Client = new S3Client({});
// snippet-end:[javascript.v3.s3.scenarios.basic.S3Client]

// snippet-start:[javascript.v3.s3.scenarios.basic.CreateBucket]
export const createBucket = async () => {
  const bucketName = await promptForText(
    "Enter a bucket name. Bucket names must be globally unique:",
  );
  const command = new CreateBucketCommand({ Bucket: bucketName });
  await s3Client.send(command);
  console.log("Bucket created successfully.\n");
  return bucketName;
};
// snippet-end:[javascript.v3.s3.scenarios.basic.CreateBucket]

// snippet-start:[javascript.v3.s3.scenarios.basic.PutObject]
export const uploadFilesToBucket = async ({ bucketName, folderPath }) => {
  console.log(`Uploading files from ${folderPath}\n`);
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
      }),
    );
    console.log(`${file.Key} uploaded successfully.`);
  }
};
// snippet-end:[javascript.v3.s3.scenarios.basic.PutObject]

// snippet-start:[javascript.v3.s3.scenarios.basic.ListObjects]
export const listFilesInBucket = async ({ bucketName }) => {
  const command = new ListObjectsCommand({ Bucket: bucketName });
  const { Contents } = await s3Client.send(command);
  const contentsList = Contents.map((c) => ` • ${c.Key}`).join("\n");
  console.log("\nHere's a list of files in the bucket:");
  console.log(contentsList + "\n");
};
// snippet-end:[javascript.v3.s3.scenarios.basic.ListObjects]

// snippet-start:[javascript.v3.s3.scenarios.basic.CopyObject]
export const copyFileFromBucket = async ({ destinationBucket }) => {
  const answer = await promptForText(
    "Would you like to copy an object from another bucket? (yes/no)",
  );

  if (answer === "no") {
    return;
  } else {
    const copy = async () => {
      try {
        const sourceBucket = await promptForText("Enter source bucket name:");
        const sourceKey = await promptForText("Enter source key:");
        const destinationKey = await promptForText("Enter destination key:");

        const command = new CopyObjectCommand({
          Bucket: destinationBucket,
          CopySource: `${sourceBucket}/${sourceKey}`,
          Key: destinationKey,
        });
        await s3Client.send(command);
        await copyFileFromBucket({ destinationBucket });
      } catch (err) {
        console.error(`Copy error.`);
        console.error(err);
        const retryAnswer = await promptForText("Try again? (yes/no)");
        if (retryAnswer !== "no") {
          await copy();
        }
      }
    };
    await copy();
  }
};
// snippet-end:[javascript.v3.s3.scenarios.basic.CopyObject]

// snippet-start:[javascript.v3.s3.scenarios.basic.GetObject]
export const downloadFilesFromBucket = async ({ bucketName }) => {
  const { Contents } = await s3Client.send(
    new ListObjectsCommand({ Bucket: bucketName }),
  );
  const path = await promptForText("Enter destination path for files:");

  for (let content of Contents) {
    const obj = await s3Client.send(
      new GetObjectCommand({ Bucket: bucketName, Key: content.Key }),
    );
    writeFileSync(
      `${path}/${content.Key}`,
      await obj.Body.transformToByteArray(),
    );
  }
  console.log("Files downloaded successfully.\n");
};
// snippet-end:[javascript.v3.s3.scenarios.basic.GetObject]

// snippet-start:[javascript.v3.s3.scenarios.basic.clean]
export const emptyBucket = async ({ bucketName }) => {
  const listObjectsCommand = new ListObjectsCommand({ Bucket: bucketName });
  const { Contents } = await s3Client.send(listObjectsCommand);
  const keys = Contents.map((c) => c.Key);

  const deleteObjectsCommand = new DeleteObjectsCommand({
    Bucket: bucketName,
    Delete: { Objects: keys.map((key) => ({ Key: key })) },
  });
  await s3Client.send(deleteObjectsCommand);
  console.log(`${bucketName} emptied successfully.\n`);
};

export const deleteBucket = async ({ bucketName }) => {
  const command = new DeleteBucketCommand({ Bucket: bucketName });
  await s3Client.send(command);
  console.log(`${bucketName} deleted successfully.\n`);
};
// snippet-end:[javascript.v3.s3.scenarios.basic.clean]

// snippet-start:[javascript.v3.s3.scenarios.basic.main]
const main = async () => {
  const OBJECT_DIRECTORY = `${dirnameFromMetaUrl(
    import.meta.url,
  )}../../../../resources/sample_files/.sample_media`;

  try {
    console.log(wrapText("Welcome to the Amazon S3 getting started example."));
    console.log("Let's create a bucket.");
    const bucketName = await createBucket();
    await promptToContinue();

    console.log(wrapText("File upload."));
    console.log(
      "I have some default files ready to go. You can edit the source code to provide your own.",
    );
    await uploadFilesToBucket({
      bucketName,
      folderPath: OBJECT_DIRECTORY,
    });

    await listFilesInBucket({ bucketName });
    await promptToContinue();

    console.log(wrapText("Copy files."));
    await copyFileFromBucket({ destinationBucket: bucketName });
    await listFilesInBucket({ bucketName });
    await promptToContinue();

    console.log(wrapText("Download files."));
    await downloadFilesFromBucket({ bucketName });

    console.log(wrapText("Clean up."));
    await emptyBucket({ bucketName });
    await deleteBucket({ bucketName });
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
