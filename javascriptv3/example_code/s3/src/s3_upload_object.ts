/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_putobject.ts uploads an existing file to an Amazon Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- OBJECT_PATH_AND_NAME: Relative path and name of object. For example '../myFiles/index.js'.

Running the code:
ts-node s3_putobject.ts

[Outputs | Returns]:
Uploads the specified file to the specified bucket.

*/
// snippet-start:[s3.JavaScript.buckets.uploadV3]
// Import required AWS SDK clients and commands for Node.js.
const { S3Client, PutObjectCommand } = require("@aws-sdk/client-s3");
const path = require("path");

// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const uploadParams = { Bucket: "BUCKET_NAME" };
const file = "OBJECT_PATH_AND_NAME"; // Path to and name of object. For example '../myFiles/index.js'.

// Create an Amazon S3 service client object.
const s3 = new S3Client({ region: REGION });

// Upload file to specified bucket.
const run = async () => {
  // Add the required 'Key' parameter using the 'path' module.
  uploadParams.Key = path.basename(file);
  try {
    const data = await s3.send(new PutObjectCommand(uploadParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[s3.JavaScript.buckets.uploadV3]

