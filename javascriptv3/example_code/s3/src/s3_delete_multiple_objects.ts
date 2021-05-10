/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_delete_multiple_objects.ts demonstrates how to delete multiple objects from an Amazon Simple Storage Solution (S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- KEY_1
- KEY_2

Running the code:
ts-node s3_delete_multiple_objects.ts
*/
// snippet-start:[s3.JavaScript.buckets.deletemultipleobjectsV3]
const { S3Client, DeleteObjectsCommand } = require("@aws-sdk/client-s3");

const REGION = "REGION"; //e.g. "us-east-1"

const params = {
  Bucket: "BUCKET_NAME",
  Delete: {
    Objects: [
      {
        Key: "KEY_1"
      },
      {
        Key: "KEY_2"
      },
    ]
  },
};

// Create Amazon S3 client service object.
const s3 = new S3Client({ region: REGION });

const run = async () => {
  try {
    const data = await s3.send(new DeleteObjectsCommand(params));
    console.log("Success. Object deleted.");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.deletemultipleobjectsV3]
module.exports ={run};
