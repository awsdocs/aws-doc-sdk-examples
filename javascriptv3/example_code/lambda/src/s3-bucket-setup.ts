/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-s3-setup.html.

Purpose:
s3-bucket-setup.ts demonstrates how to create an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
ts-node s3-bucket-setup.ts
 */
// snippet-start:[lambda.JavaScript.tutorial.BucketSetUpV3]

// Import an S3 client
const {
  S3Client,
  CreateBucketCommand,
  PutBucketWebsiteCommand,
  PutBucketPolicyCommand
} = require("@aws-sdk/client-s3");

// Set the AWS Region
const REGION = "REGION"; //e.g. "us-east-1"

// Create params JSON for S3.createBucket
const bucketName = "BUCKET_NAME"; //BUCKET_NAME
const bucketParams = {
  Bucket: bucketName
};

// Create params JSON for S3.setBucketWebsite
const staticHostParams = {
  Bucket: bucketName,
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: "error.html",
    },
    IndexDocument: {
      Suffix: "index.html",
    },
  },
};

var readOnlyAnonUserPolicy = {
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
const bucketResource = "arn:aws:s3:::" + bucketName + "/*"; //BUCKET_NAME
readOnlyAnonUserPolicy.Statement[0].Resource[0] = bucketResource;

// convert policy JSON into string and assign into params
const bucketPolicyParams = {
  Bucket: bucketName,
  Policy: JSON.stringify(readOnlyAnonUserPolicy)
};

// Instantiate an S3 client
const s3 = new S3Client({ region: REGION });

const run = async () => {
  try {
    // Call S3 to create the bucket
    const response = await s3.send(new CreateBucketCommand(bucketParams));
    console.log("Bucket URL is ", response.Location);
  } catch (err) {
    console.log("Error", err);
  }
  try {
    // Set the new policy on the newly created bucket
    const response = await s3.send(
      new PutBucketWebsiteCommand(staticHostParams)
    );
    // Update the displayed policy for the selected bucket
    console.log("Success", response);
  } catch (err) {
    // Display error message
    console.log("Error", err);
  }
};

run();
// snippet-end:[lambda.JavaScript.tutorial.BucketSetUpV3]

