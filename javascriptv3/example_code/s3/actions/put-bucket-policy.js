/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
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
// Import required AWS SDK clients and commands for Node.js.
import { CreateBucketCommand, PutBucketPolicyCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

const BUCKET_NAME = "BUCKET_NAME";
export const bucketParams = {
  Bucket: BUCKET_NAME,
};
// Create the policy in JSON for the S3 bucket.
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

// Create selected bucket resource string for bucket policy.
const bucketResource = "arn:aws:s3:::" + BUCKET_NAME + "/*"; //BUCKET_NAME
readOnlyAnonUserPolicy.Statement[0].Resource[0] = bucketResource;

// Convert policy JSON into string and assign into parameters.
const bucketPolicyParams = {
  Bucket: BUCKET_NAME,
  Policy: JSON.stringify(readOnlyAnonUserPolicy),
};

export const run = async () => {
  try {
    const data = await s3Client.send(
        new CreateBucketCommand(bucketParams)
    );
    console.log('Success, bucket created.', data)
    try {
      const response = await s3Client.send(
          new PutBucketPolicyCommand(bucketPolicyParams)
      );
      console.log("Success, permissions added to bucket", response);
      return response;
    }
    catch (err) {
        console.log("Error adding policy to S3 bucket.", err);
      }
  } catch (err) {
    console.log("Error creating S3 bucket.", err);
  }
};
run();
// snippet-end:[s3.JavaScript.policy.putBucketPolicyV3]

