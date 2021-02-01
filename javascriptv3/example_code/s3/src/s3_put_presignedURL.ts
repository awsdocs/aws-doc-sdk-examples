/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_put_presignedURL.ts creates a presigned URL to upload a file to an Amazon Simple Storage Service (Amazon S3) bucket.

Note: This example immediately deletes the object and bucket.

Inputs (replace in code):
- REGION


Running the code:
ts-node s3_put_presignedURL.ts
[Outputs | Returns]:
Uploads the specified file to the specified bucket.
*/

// snippet-start:[s3.JavaScript.buckets.presignedurlv3]
// Import the required AWS SDK clients and commands for Node.js
const {
  S3,
  CreateBucketCommand,
  PutObjectCommand,
  GetObjectCommand,
  DeleteObjectCommand,
  DeleteBucketCommand,
} = require("@aws-sdk/client-s3");
const { getSignedUrl  } = require("@aws-sdk/s3-request-presigner");


// Set parameters
// Create random names for the Amazon Simple Storage Service (Amazon S3) bucket and key.
const params = {
  Bucket: `test-bucket-${Math.ceil(Math.random() * 10 ** 10)}`,
  Key: `test-object-${Math.ceil(Math.random() * 10 ** 10)}`,
  Body: "BODY",
  Region: "eu-west-1",
};

// Create an Amazon S3 service client object.
const s3Client = new S3({ region: params.Region });

const run = async () => {
  // Create an Amazon S3 bucket.
  try {
    console.log(`Creating bucket ${params.Bucket}`);
    const data = await s3Client.send(
        new CreateBucketCommand({ Bucket: params.Bucket })
    );
    console.log(`Waiting for "${params.Bucket}" bucket creation...\n`);
  } catch (err) {
    console.log("Error creating bucket", err);
  }
  // Put the object in the Amazon S3 bucket.
  try {
    console.log(`Putting object "${params.Key}" in bucket`);
    const data = await s3Client.send(
        new PutObjectCommand({
          Bucket: params.Bucket,
          Key: params.Key,
          Body: params.Body,
        })
    );
  } catch (err) {
    console.log("Error putting object", err);
  }
  // Create a presigned URL.
  try {
    // Create the command.
    const command = new GetObjectCommand(params);
    // Create the presigned URL.
    const signedUrl = await getSignedUrl(s3Client, command, {
      expiresIn: 3600,
    });
    console.log(
        `\nGetting "${params.Key}" using signedUrl with body "${params.Body}" in v3`
    );
    console.log(signedUrl);
  }
  catch (err) {
    console.log("Error creating presigned URL", err);
  }
  // Delete the object.
  try {
    console.log(`\nDeleting object "${params.Key}" from bucket`);
    const data = await s3Client.send(
        new DeleteObjectCommand({ Bucket: params.Bucket, Key: params.Key })
    );
  } catch (err) {
    console.log("Error deleting object", err);
  }
  // Delete the bucket.
  try {
    console.log(`\nDeleting bucket ${params.Bucket}`);
    const data = await s3Client.send(
        new DeleteBucketCommand({ Bucket: params.Bucket, Key: params.Key })
    );
  } catch (err) {
    console.log("Error deleting object", err);
  }
};
run();

// snippet-end:[s3.JavaScript.buckets.presignedurlv3]
