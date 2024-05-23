
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { fileURLToPath } from "url";
import { PutObjectLockConfigurationCommand, S3Client } from "@aws-sdk/client-s3";

/**
 * @param {S3Client} client 
 * @param {string} bucketName
 */
export const main = async (client, bucketName) => {
  // snippet-start:[s3.JavaScript.buckets.putObjectLockConfigurationV3]
  const command = new PutObjectLockConfigurationCommand({
    Bucket: bucketName,
    // The Object Lock configuration that you want to apply to the specified bucket.
    ObjectLockConfiguration: {
      ObjectLockEnabled: "Enabled",
    },
    // Optionally, you can provide additional parameters
    // ExpectedBucketOwner: "ACCOUNT_ID",
    // RequestPayer: "requester",
    // Token: "OPTIONAL_TOKEN",
  });

  try {
    const response = await client.send(command);
    console.log(
      `Object Lock Configuration updated: ${response.$metadata.httpStatusCode}`
    );
  } catch (err) {
    console.error(err);
  }
  // snippet-end:[s3.JavaScript.buckets.putObjectLockConfigurationV3]
};

// Invoke main function if this file was run directly.
if (process.argv[1] === fileURLToPath(import.meta.url)) {
  main(new S3Client(), "BUCKET_NAME");
}

