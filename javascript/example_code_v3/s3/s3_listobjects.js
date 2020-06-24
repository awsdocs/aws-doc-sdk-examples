/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_listobjects.js demonstrates how to list the objects in an Amazon S3 bucket.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)

Running the code:
node s3_listobjects.js REGION BUCKET_NAME
*/
// snippet-start:[s3.JavaScript.v3.buckets.listObjects]

// Import required AWS-SDK clients and commands for Node.js
const { S3 } = require("@aws-sdk/client-s3");
// Set the AWS region
const region = process.argv[2];
// Create S3 service object
const s3 = new S3(region);
// Create the parameters for the bucket
const bucketParams = {Bucket : process.argv[3]};

async function run() {
  try {
    const data = await s3.listObjects(bucketParams);
    console.log('Success', data);
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.v3.buckets.listObjects]
//for unit tests only
exports.run = run;

