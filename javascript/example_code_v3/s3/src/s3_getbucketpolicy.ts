/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-bucket-policies.html.

Purpose:
s3_getbucketpolicy.ts demonstrates how to retrieve the policy of an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
ts-node s3_getbucketpolicy.ts

*/
// snippet-start:[s3.JavaScript.policy.getBucketPolicyV3]

// Import required AWS SDK clients and commands for Node.js
const { S3 } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Create the parameters for calling
const bucketParams = { Bucket: "BUCKET_NAME" };

// Create S3 service object
const s3 = new S3(REGION);

const run = async () => {
  try {
    const data = await s3.getBucketPolicy(bucketParams);
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.policy.getBucketPolicyV3]
//for unit tests only
// module.exports = {run};
