/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_delete_multiple_objects.js demonstrates how to delete multiple objects} from an Amazon Simple Storage Solution (S3) bucket.

Inputs (replace in code):
- BUCKET_NAME
- KEY_1
- KEY_2

Running the code:
nodes3_delete_multiple_objects.js
*/
// snippet-start:[s3.JavaScript.buckets.deletemultipleobjectsV3]
import { DeleteObjectsCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js" // Helper function that creates an Amazon S3 service client module.

export const bucketParams = {
  Bucket: "BUCKET_NAME",
  Delete: {
    Objects: [
      {
        Key: "KEY_1",
      },
      {
        Key: "KEY_2",
      },
    ],
  },
};

export const run = async () => {
  try {
    const data = await s3Client.send(new DeleteObjectsCommand(bucketParams));
    return data; // For unit tests.
    console.log("Success. Object deleted.");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.deletemultipleobjectsV3]
// For unit testing only.
// module.exports ={run, bucketParams};

