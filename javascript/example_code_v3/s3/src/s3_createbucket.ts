/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_createbucket.ts demonstrates how to create an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
ts-node s3_createbucket.ts
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
// snippet-start:[s3.JavaScript.buckets.createBucketV3]
//for unit tests only
export = { run };
