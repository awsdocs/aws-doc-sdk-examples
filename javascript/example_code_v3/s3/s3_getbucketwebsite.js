/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-static-web-host.html.

Purpose:
s3_getbucketwebsite.js demonstrates how to retrieve the website configuration of an Amazon S3 bucket.

Inputs (into command line below):
- REGION
- BUCKET_NAME

Running the code:
node S3.js  REGION BUCKET_NAME

// snippet-start:[s3.JavaScript.v3.website.getBucketWebsite]
 */

// Import required AWS SDK clients and commands for Node.js
const { S3, GetBucketWebsiteCommand } = require("@aws-sdk/client-s3");
// Set the AWS region
const region = process.argv[2];
// Create S3 service object
const s3 = new S3(region);
// Create the parameters for calling
const bucketParams = {Bucket : process.argv[3]};

async function run(){
  try{
    const data = await s3.send(new GetBucketWebsiteCommand(bucketParams));
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.v3.website.getBucketWebsite]
//for unit tests only
exports.run = run;
