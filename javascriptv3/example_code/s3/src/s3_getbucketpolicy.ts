/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is pending release.  The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for v3 is also
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
const { S3Client, GetBucketPolicyCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Create the parameters for calling
const bucketParams = { Bucket: "BUCKET_NAME" };

// Create S3 service object
const s3 = new S3Client(REGION);

const run = async () => {
  try {
    const data = await s3.send(new GetBucketPolicyCommand(bucketParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.policy.getBucketPolicyV3]

