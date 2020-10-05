/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The preview version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html.

Purpose:
sample.ts demonstrates how to get started using node.js with the AWS SDK for JavaScript.

Inputs (replace in code):
 - REGION
 - BUCKET_NAME

Running the code:
ts-node sample.ts
*/
// snippet-start:[GettingStarted.JavaScript.NodeJS.sampleV3]
// Import required AWS SDK clients and commands for Node.js
const {
  S3Client,
  PutObjectCommand,
  CreateBucketCommand
} = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; // e.g., "us-east-1"

// Set the bucket parameters
const bucketName = "BUCKET_NAME";
const bucketParams = { Bucket: bucketName };

// Create name for uploaded object key
const keyName = "hello_world.txt";
const objectParams = { Bucket: bucketName, Key: keyName, Body: "Hello World!" };

// Create an S3 client service object
const s3 = new S3Client(REGION);

const run = async () => {
  // Create S3 bucket
  try {
    const data = await s3.send(new CreateBucketCommand(bucketParams));
    console.log("Success. Bucket created.");
  } catch (err) {
    console.log("Error", err);
  }
  try {
    const results = await s3.send(new PutObjectCommand(objectParams));
    console.log("Successfully uploaded data to " + bucketName + "/" + keyName);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[GettingStarted.JavaScript.NodeJS.sampleV3]

