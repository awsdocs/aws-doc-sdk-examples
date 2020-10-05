/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_createbucket.ts is part of a tutorial demonstrating how to build and deploy an app to submit
data to an Amazon DynamoDB table.To run the full tutorial, see
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-submitting-data.html.
s3_createbucket.ts demonstrates how to create an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
node s3_createbucket.js
*/

// snippet-start:[s3.JavaScript.buckets.createBucketV3]

// Import required AWS SDK clients and commands for Node.js
const { S3Client, CreateBucketCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the bucket parameters
const bucketParams = { Bucket: "BUCKET_NAME" };

// Create S3 service object
const s3 = new S3Client(REGION);

//Attempt to create the bucket
const run = async () => {
  try {
    const data = await s3.send(new CreateBucketCommand(bucketParams));
    console.log("Success", data.$metadata.httpHeaders.location);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.createBucketV3]
//for unit tests only
exports.run = run;
