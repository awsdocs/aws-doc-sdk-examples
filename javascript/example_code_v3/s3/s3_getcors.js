/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) topic
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-configuring-buckets.html.

Purpose:
s3_getcors.js demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.

Inputs (into command line below):
- REGION
- BUCKET_NAME

Running the code:
node s3_getcors.js REGION BUCKET_NAME
 */
// snippet-start:[s3.JavaScript.v3.cors.getBucketCors]

// Import required AWS SDK clients and commands for Node.js
const { S3, GetBucketCorsCommand } = require("@aws-sdk/client-s3");
// Set the AWS region
const region = process.argv[2];
// Create S3 service object
const s3 = new S3(region);
// Create the parameters for calling
const bucketParams = {Bucket : process.argv[3]};

async function run(){
  try{
    const data = await s3.send(new GetBucketCorsCommand(bucketParams));
    console.log('Success', JSON.stringify(data.CORSRules));
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.v3.cors.getBucketCors]
//for unit testing only
exports.run = run;

