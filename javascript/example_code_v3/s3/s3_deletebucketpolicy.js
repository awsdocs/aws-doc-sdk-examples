/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-bucket-policies.html.

Purpose:
s3_deletebucketpolicy.js demonstrates how to delete an Amazon S3 bucket policy.]

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)

Running the code:
node s3_deletebucketpolicy.js REGION BUCKET_NAME
*/
// snippet-start:[s3.JavaScript.v3.policy.deleteBucketPolicy]

// Import required AWS-SDK clients and commands for Node.js
const  {S3}  = require('@aws-sdk/client-s3/');
// Create S3 service object
const region = process.argv[2];
const s3 = new S3(region);
// Set the bucket parameters
const bucketParams = {Bucket: process.argv[3]};

async function run(){
    try {
        const data =  await s3.deleteBucketPolicy(bucketParams);
        console.log('Success', data +', bucket policy deleted');
    }
    catch (err) {
        console.log('Error', err);
    }
};
run();
// snippet-end:[s3.JavaScript.v3.policy.deleteBucketPolicy]
exports.run = run; //for unit tests only
