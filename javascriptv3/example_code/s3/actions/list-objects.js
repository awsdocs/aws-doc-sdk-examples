/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { fileURLToPath } from "url";

// snippet-start:[s3.JavaScript.buckets.listObjectsV3]
import { ListObjectsCommand, S3Client } from "@aws-sdk/client-s3";

const client = new S3Client({});

export const main = async () => {
  const command = new ListObjectsCommand({
    Bucket: "test-bucket-corey",
  });

  try {
    const { Contents } = await client.send(command);
    const contentsList = Contents.map((c) => ` • ${c.Key}`).join("\n");
    console.log("Your bucket contains the following objects:\n", contentsList);
  } catch (err) {
    console.error(err);
  }
};
// snippet-end:[s3.JavaScript.buckets.listObjectsV3]

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main();
}
