/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-s3-setup.html.

Purpose:
s3-bucket-setup-non-modular.ts demonstrates how to create an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
ts-node s3-bucket-setup-non-modular.ts
*/
// snippet-start:[lambda.JavaScript.BucketSetUp.NonModularV3]

// Import a non-modular S3 client
const { S3Client } = require("@aws-sdk/client-s3");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create params JSON for S3.createBucket
const bucketParams = {
  Bucket: "BUCKET_NAME",
};

// Create params JSON for S3.setBucketWebsite
const staticHostParams = {
  Bucket: process.argv[3],
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: "error.html",
    },
    IndexDocument: {
      Suffix: "index.html",
    },
  },
};

// Instantiate the S3 client
const s3 = new S3Client({ region: REGION });

const run = async () => {
  // Call S3 to create the bucket
  try {
    await s3.createBucket(bucketParams);
    console.log("Success, bucket created");
  } catch (err) {
    console.log("Error", err);
  }
  try {
    // Update the displayed policy for the selected bucket
    await s3.putBucketWebsite(staticHostParams);
    console.log("Success, bucket policy updated");
  } catch (err) {
    console.log(err);
  }
};
run();
// snippet-end:[lambda.JavaScript.BucketSetUp.NonModularV3]

