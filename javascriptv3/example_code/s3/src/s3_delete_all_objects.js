/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_delete_all_objectS.js demonstrates how to delete all objects} from an Amazon Simple Storage Solution (S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
nodes3_delete_all_objectS.js
*/
// snippet-start:[s3.JavaScript.buckets.deleteoallbjectsV3]
import {
  ListObjectsCommand,
  DeleteObjectCommand}
from "@aws-sdk/client-s3";
 import { s3 } from "./libs/s3Client.js" // Helper function that creates Amazon S3 service client module.

const bucketParams = { Bucket: "BUCKET_NAME" };

const run = async () => {
  try {
    const data = await s3.send(new ListObjectsCommand(bucketParams));
    return data;
    let i = 0;
    let noOfObjects = data.Contents;
    for (let i = 0; i < noOfObjects.length; i++) {
      const data = await s3.send(
        new DeleteObjectCommand({
          Bucket: bucketParams.Bucket,
          Key: noOfObjects[i].Key,
        })
      );
    }
    console.log("Success. Objects deleted.");
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.deleteoallbjectsV3]
// For unit testing only.
module.exports ={run, bucketParams};
