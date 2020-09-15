/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/cloud9/latest/user-guide/sample-nodejs.html.

Purpose:
s3.ts demonstrates how to list, create, and delete a bucket in Amazon S3.

Inputs (replace in command line input below):
- REGION
- BUCKET_NAME

Running the code:
ts-node s3.ts REGION BUCKET_NAME
*/

// snippet-start:[s3.javascript.bucket_operations.list_create_deleteV3]

if (process.argv.length < 4) {
  console.log(
    "Usage: node s3.js <the bucket name> <the AWS Region to use>\n" +
      "Example: node s3.js my-test-bucket us-east-2"
  );
  process.exit(1);
}
const { S3 } = require("@aws-sdk/client-s3");
const async = require("async"); // To call AWS operations asynchronously.

var bucket_name = process.argv[2];
var region = process.argv[3];

var s3 = new S3({ region });

var create_bucket_params = {
  Bucket: bucket_name,
  CreateBucketConfiguration: {
    LocationConstraint: region,
  },
};

var delete_bucket_params = { Bucket: bucket_name };

// List all of your available buckets in this AWS Region.

const run = async () => {
  try {
    const data = await s3.listBuckets({});
    console.log("My buckets now are:\n");

    for (var i = 0; i < data.Buckets.length; i++) {
      console.log(data.Buckets[i].Name);
    }
  } catch (err) {
    console.log("Error", err);
  }
  try {
    console.log("\nCreating a bucket named " + bucket_name + "...\n");
    const data = await s3.createBucket(create_bucket_params);
    console.log("My buckets now are:\n");

    for (var i = 0; i < data.Buckets.length; i++) {
      console.log(data.Buckets[i].Name);
    }
  } catch (err) {
    console.log(err.code + ": " + err.message);
  }
  try {
    console.log("\nDeleting the bucket named " + bucket_name + "...\n");
    const data = await s3.deleteBucket(delete_bucket_params);
  } catch (err) {
    console.log(err.code + ": " + err.message);
  }
};
run();

// snippet-end:[s3.javascript.bucket_operations.list_create_deleteV3]
//for unit tests only
// module.exports = {run};
