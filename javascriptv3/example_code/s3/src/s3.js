/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/cloud9/latest/user-guide/sample-nodejs.html.

Purpose:
s3.js demonstrates how to list, create, and delete a bucket in Amazon S3.

Inputs:
- BUCKET_NAME

Running the code:
nodes3.js REGION BUCKET_NAME
*/

// snippet-start:[s3.javascript.bucket_operations.list_create_deleteV3]

if (process.argv.length < 4) {
  console.log(
    "Usage: node s3.js <the bucket name> <the AWS Region to use>\n" +
      "Example: node s3.js my-test-bucket us-east-2"
  );
  process.exit(1);
}
import {
  ListBucketsCommand,
  CreateBucketCommand,
  DeleteBucketCommand,
} from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client"; // Helper function that creates an Amazon S3 service client module.

const bucket_name = process.argv[2];
const region = process.argv[3];

const create_bucket_params = {
  Bucket: bucket_name,
  CreateBucketConfiguration: {
    LocationConstraint: region,
  },
};

export const delete_bucket_params = { Bucket: bucket_name };

// List all of your available buckets in this AWS Region.

export const run = async () => {
  try {
    const data = await s3Client.send(new ListBucketsCommand({}));
    console.log("My buckets now are:\n");

    for (var i = 0; i < data.Buckets.length; i++) {
      console.log(data.Buckets[i].Name);
    }
  } catch (err) {
    console.log("Error", err);
  }

  try {
    console.log("\nCreating a bucket named " + bucket_name + "...\n");
    const data = await s3Client.send(
      new CreateBucketCommand(create_bucket_params)
    );
    console.log("My buckets now are:\n");

    for (var i = 0; i < data.Buckets.length; i++) {
      console.log(data.Buckets[i].Name);
    }
  } catch (err) {
    console.log(err.code + ": " + err.message);
  }

  try {
    console.log("\nDeleting the bucket named " + bucket_name + "...\n");
    const data = await s3Client.send(
      new DeleteBucketCommand(delete_bucket_params)
    );
    return data;
  } catch (err) {
    console.log(err.code + ": " + err.message);
  }
};
run();

// snippet-end:[s3.javascript.bucket_operations.list_create_deleteV3]
// For unit testing only.
// module.exports ={run, create_bucket_params, delete_bucket_params};
