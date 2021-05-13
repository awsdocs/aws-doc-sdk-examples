/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html.

Purpose:
sample.ts demonstrates how to get started using node.js with the AWS SDK for JavaScript.

Inputs (replace in code):
 - REGION
 - BUCKET_NAME
 - KEY
 - BODY

Running the code:
node sample.js
*/
// snippet-start:[GettingStarted.JavaScript.NodeJS.sampleV3]
// Import required AWS SDK clients and commands for Node.js.
const {
  S3Client,
  PutObjectCommand,
  CreateBucketCommand
} = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; // The AWS Region. For example, "us-east-1".

// Set the parameters
const params = {
  Bucket: "BUCKET_NAME", // The name of the bucket. For example, 'sample_bucket_101'.
  Key: "KEY", // The name of the object. For example, 'sample_upload.txt'.
  Body: "BODY" // The content of the object. For example, 'Hello world!".
}


// Create an Amazon Simple Storage Solutiou (Amazon S3) client service object.
const s3Client = new S3Client({ region: REGION });

const run = async () => {
  // Create an Amazon S3 bucket.
  try {
    const data = await s3Client.send(new CreateBucketCommand({Bucket: params.Bucket}));
    console.log(data);
    console.log("Successfully created a bucket called ", data.BucketName);
  } catch (err) {
    console.log("Error", err);
  }
  // Create an object and upload it to the Amazon S3 bucket.
  try {
    const results = await s3Client.send(new PutObjectCommand(params));
    console.log("Successfully created " + params.Key + " and uploaded it to " + params.Bucket + "/" + params.Key).;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[GettingStarted.JavaScript.NodeJS.sampleV3]

