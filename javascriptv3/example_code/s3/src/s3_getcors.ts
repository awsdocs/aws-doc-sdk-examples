/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-configuring-buckets.html.
Purpose:
s3_getcors.ts demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.
Inputs :
- REGION
- BUCKET_NAME
Running the code:
ts-node s3_getcors.ts
 */
// snippet-start:[s3.JavaScript.cors.getBucketCorsV3]

// Import required AWS SDK clients and commands for Node.js
const { S3Client, GetBucketCorsCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Create the parameters for calling
const bucketParams = { Bucket: "BUCKET_NAME" };

// Create S3 service object
const s3 = new S3Client(REGION);

const run = async () => {
  try {
    const data = await s3.send(new GetBucketCorsCommand(bucketParams));
    console.log("Success", JSON.stringify(data.CORSRules));
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.cors.getBucketCorsV3]
//for unit testing only
export = { run };
