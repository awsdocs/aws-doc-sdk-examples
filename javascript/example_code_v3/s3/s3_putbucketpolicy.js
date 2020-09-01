/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-bucket-policies.html.

Purpose:
s3_putbucketpolicy.js demonstrates how to set a policy on an Amazon S3 bucket to enable anybody read access permissions.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
node s3_putbucketpolicy.js
*/
// snippet-start:[s3.JavaScript.buckets.putbucketpolicyV3]
// Import an S3 client
const { S3, S3Client } = require("@aws-sdk/client-s3");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"
// Create params JSON for S3.createBucket
const BUCKET_NAME = "BUCKET_NAME";
const bucketParams = {
  Bucket: BUCKET_NAME,
};
const readOnlyAnonUserPolicy = {
  Version: "2012-10-17",
  Statement: [
    {
      Sid: "AddPerm",
      Effect: "Allow",
      Principal: "*",
      Action: ["s3:GetObject"],
      Resource: [""],
    },
  ],
};

// Create selected bucket resource string for bucket policy
const bucketResource = "arn:aws:s3:::" + BUCKET_NAME + "/*"; //BUCKET_NAME
readOnlyAnonUserPolicy.Statement[0].Resource[0] = bucketResource;

// Convert policy JSON into string and assign into params
const bucketPolicyParams = {
  Bucket: BUCKET_NAME,
  Policy: JSON.stringify(readOnlyAnonUserPolicy),
};

// Instantiate an S3 client
const s3Client = new S3Client(REGION);
const s3 = new S3({});

const run = async () => {
  try {
    const data = await s3.putBucketPolicy(bucketPolicyParams);
    console.log("Success, permissions added to bucket");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.putbucketpolicyV3]
//for unit tests only
exports.run = run;
