/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/cross-service-example-dataupload.html.

Purpose:
s3_putbucketpolicy.js is an example that demonstrates how to attach a permissions policy to an Amazon S3 bucket.

Inputs (replace in code):
- IDENTITY_POOL_ID
- TABLE_NAME

Running the code:
node s3_putbucketpolicy.js
 */
// snippet-start:[s3.JavaScript.policy.putBucketPolicyV3]
// Import required AWS SDK clients and commands for Node.js
import { PutBucketPolicyCommand } from "@aws-sdk/client-s3";
 import { s3Client } from "./libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.

// Create params JSON for S3.createBucket
const BUCKET_NAME = "BUCKET_NAME";
const bucketParams = {
  Bucket: BUCKET_NAME,
};
// Create the policy
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

// create selected bucket resource string for bucket policy
const bucketResource = "arn:aws:s3:::" + BUCKET_NAME + "/*"; //BUCKET_NAME
readOnlyAnonUserPolicy.Statement[0].Resource[0] = bucketResource;

// // convert policy JSON into string and assign into params
const bucketPolicyParams = {
  Bucket: BUCKET_NAME,
  Policy: JSON.stringify(readOnlyAnonUserPolicy),
};

const run = async () => {
  try {
    // const response = await s3.putBucketPolicy(bucketPolicyParams);
    const response = await s3Client.send(
      new PutBucketPolicyCommand(bucketPolicyParams)
    );
    return response;
    console.log("Success, permissions added to bucket", response);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.policy.putBucketPolicyV3]
// For unit testing only.
// module.exports ={run, bucketPolicyParams};
