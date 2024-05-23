// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { fileURLToPath } from "url";
import { GetObjectLockConfigurationCommand, S3Client } from "@aws-sdk/client-s3";

/**
 * @param {S3Client} client 
 * @param {string} bucketName
 */
export const main = async (client, bucketName) => {
  // snippet-start:[s3.JavaScript.buckets.getObjectLockConfigurationV3]
  const command = new GetObjectLockConfigurationCommand({
    Bucket: bucketName,
    // Optionally, you can provide additional parameters
    // ExpectedBucketOwner: "ACCOUNT_ID",
  });

  try {
    const { ObjectLockConfiguration } = await client.send(command);
    console.log(`Object Lock Configuration: ${ObjectLockConfiguration}`);
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[s3.JavaScript.buckets.getObjectLockConfigurationV3]
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main(new S3Client(), "BUCKET_NAME");
}