/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-s3-setup.html.

Purpose:
    s3-bucket-setup.js demonstrates how to create an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
node s3-bucket-setup.js
 */
// snippet-start:[lambda.JavaScript.BucketSetUpV3]

// Import an S3 client
const {
  S3Client,
  CreateBucketCommand,
  PutBucketWebsiteCommand,
} = require("@aws-sdk/client-s3");

// Set the AWS Region
const REGION = "region"; //e.g. "us-east-1"

// Create params JSON for S3.createBucket
const bucketName = "BUCKET_NAME"; //BUCKET_NAME
const bucketParams = {
  Bucket: bucketName,
};

// Create params JSON for S3.setBucketWebsite
const staticHostParams = {
  Bucket: bucketName,
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: "error.html",
    },
    IndexDocument: {
      Suffix: "index.html",
    },
  },
};

// Instantiate an S3 client
const s3 = new S3Client(REGION);

const run = async () => {
  try {
    // Call S3 to create the bucket
    const response = await s3.send(new CreateBucketCommand(bucketParams));
    console.log("Bucket URL is ", response.Location);
  } catch (err) {
    console.log("Error", err);
  }
  try {
    // Set the new policy on the newly created bucket
    const response = await s3.send(
      new PutBucketWebsiteCommand(staticHostParams)
    );
    // Update the displayed policy for the selected bucket
    console.log("Success", response);
  } catch (err) {
    // Display error message
    console.log("Error", err);
  }
};

run();
// snippet-end:[lambda.JavaScript.BucketSetUpV3]
