/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_createbucket.js demonstrates how to create an Amazon S3 bucket.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)

Running the code:
node s3_createbucket.js REGION BUCKET_NAME
*/

// snippet-start:[s3.JavaScript.v3.buckets.deleteBucket]

// Import required AWS SDK clients and commands for Node.js
const { S3 } = require("@aws-sdk/client-s3");
// Set the AWS region
const region = process.argv[2];
// Create S3 service object
const s3 = new S3(region);
// Set the bucket parameters
const bucketParams= {Bucket: process.argv[3]};

//Attempt to create the bucket
async function run(){
    try{
        const data = await s3.createBucket(bucketParams)
        console.log('Success', data.$metadata.httpHeaders.location);
    }
    catch (err){
        console.log('Error', err);
    }
};
run();
// snippet-end:[s3.JavaScript.v3.buckets.deleteBucket]
//for unit tests only
exports.run = run;
