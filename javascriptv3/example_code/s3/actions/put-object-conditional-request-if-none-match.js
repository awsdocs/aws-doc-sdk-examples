// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  PutObjectCommand,
  S3Client,
  S3ServiceException,
} from "@aws-sdk/client-s3";
import { readFile } from "node:fs/promises";

/**
 * Get a single object from a specified S3 bucket.
 * @param {{ destinationBucketName: string }}
 */
export const main = async ({ destinationBucketName }) => {
  const client = new S3Client({});
  const filePath = "./text01.txt";
  try {
    await client.send(
      new PutObjectCommand({
        Bucket: destinationBucketName,
        Key: "text01.txt",
        Body: await readFile(filePath),
        IfNoneMatch: "*",
      }),
    );
    console.log(
      "File written to bucket because the key name is not a duplicate.",
    );
  } catch (caught) {
    if (caught instanceof S3ServiceException) {
      console.error(
        `Error from S3 while uploading object to bucket. ${caught.name}: ${caught.message}`,
      );
    } else {
      throw caught;
    }
  }
};

// Call function if run directly
import { parseArgs } from "node:util";
import {
  isMain,
  validateArgs,
} from "@aws-doc-sdk-examples/lib/utils/util-node.js";

const loadArgs = () => {
  const options = {
    destinationBucketName: {
      type: "string",
      required: true,
    },
  };
  const results = parseArgs({ options });
  const { errors } = validateArgs({ options }, results);
  return { errors, results };
};

if (isMain(import.meta.url)) {
  const { errors, results } = loadArgs();
  if (!errors) {
    main(results.values);
  } else {
    console.error(errors.join("\n"));
  }
}
