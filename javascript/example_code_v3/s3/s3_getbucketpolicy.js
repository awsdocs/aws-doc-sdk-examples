/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-bucket-policies.html.

Purpose:
s3_getbucketpolicy.js demonstrates how to retrieve the policy of an Amazon S3 bucket.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)

Running the code:
node s3_getbucketpolicy.js REGION BUCKET_NAME

*/
// snippet-start:[s3.JavaScript.v3.policy.getBucketPolicy]

// Import required AWS-SDK clients and commands for Node.js
const { S3 } = require("@aws-sdk/client-s3");
// Set the AWS region
const region = process.argv[2];
// Create S3 service object
const s3 = new S3(region);
// Create the parameters for calling
const bucketParams = {Bucket : process.argv[3]};

async function run(){
  try{
    const data = await s3.getBucketPolicy(bucketParams);
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
}
run();
// snippet-end:[s3.JavaScript.v3.policy.getBucketPolicy]
//for unit tests only
exports.run = run;

