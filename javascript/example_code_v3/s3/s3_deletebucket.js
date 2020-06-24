/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_deletebucket.js demonstrates how to delete an Amazon S3 bucket.

Inputs (into command line below):
- REGION
- BUCKET_NAME

Running the code:
node s3_delete.js REGION BUCKET_NAME

*/
// snippet-start:[s3.JavaScript.v3.buckets.deleteBucket]

// Import required AWS-SDK clients and commands for Node.js
const  {S3}  = require('@aws-sdk/client-s3/');
// Set the AWS region
const region = process.argv[2];
// Create S3 service object
const s3 = new S3();
// Set the bucket parameters
const bucketParams = {Bucket : process.argv[3]};

async function run() {
  try {
    const data =  await s3.deleteBucket(bucketParams);
    console.log('Success - bucket deleted')
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.v3.buckets.deleteBucket]
//for unit tests only
exports.run = run;
