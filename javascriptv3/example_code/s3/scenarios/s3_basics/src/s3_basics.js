/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
user guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html

Purpose:
Shows how to use AWS SDK for JavaScript (v3) to get started using Amazon Simple Storage
Service (Amazon S3). Create a bucket, move objects into and out of it, and delete all
resources at the end of the demo.

Inputs (in command line):
node s3_basics.js <the bucket name> <the AWS Region to use> <object name> <object content>
*/

import { CreateBucketCommand } from "@aws-sdk/client-s3";
import { s3Client, REGION } from "../libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.
import { uploadObject } from "./helpers/uploadObject.js"; // Helper function that creates and uploads and object to Amazon.

if (process.argv.length < 5) {
  console.log(
    "Usage: node s3_basics.js <the bucket name> <the AWS Region to use> <object name> <object content>\n" +
      "Example: node s3_basics.js test-bucket test.txt 'Test Content'"
  );
  // process.exit(1);
}

// Create constants from the command line inputs.
export const bucket_name = process.argv[2];
export const object_key = process.argv[3];
export const object_content = process.argv[4];

// Set the parameters for creating a bucket.
export const create_bucket_params = {
  Bucket: bucket_name,
  CreateBucketConfiguration: {
    LocationConstraint: REGION,
  },
};

export const createBucket = async () => {
  try {
    console.log("\nCreating the bucket, named " + bucket_name + "...\n");
    const data = await s3Client.send(
      new CreateBucketCommand(create_bucket_params)
    );
    console.log("Bucket created at ", data.Location);
    try {
      uploadObject();
    } catch (err) {
      console.log("Error uploading  object", err);
    }
    return data; // For unit tests.
  } catch (err) {
    console.log("Error creating  bucket", err);
  }
};
createBucket();
