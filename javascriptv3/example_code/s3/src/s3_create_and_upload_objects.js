/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_create_and_upload_objects.js creates and uploads an object to an Amazon Simple Storage Solution (Amazon S3) bucket.
Inputs:
- BUCKET_NAME
- KEY: The name of the object to create and upload.
- BODY: The contents of the uploaded file.

Running the code:
nodes3_create_and_upload_object.js
*/
// snippet-start:[s3.JavaScript.buckets.upload_putcommandV3]

// Import required AWS SDK clients and commands for Node.js.
import { PutObjectCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

// Set the parameters.
export const bucketParams = {
  Bucket: "BUCKET_NAME",
  // Specify the name of the new object. For example, 'index.html'.
  // To create a directory for the object, use '/'. For example, 'myApp/package.json'.
  Key: "OBJECT_NAME",
  // Content of the new object.
  Body: "BODY",
};

// Create and upload the object to the S3 bucket.
export const run = async () => {
  try {
    const data = await s3Client.send(new PutObjectCommand(bucketParams));
    return data; // For unit tests.
    console.log(
      "Successfully uploaded object: " +
        bucketParams.Bucket +
        "/" +
        bucketParams.Key
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[s3.JavaScript.buckets.upload_putcommandV3]
// For unit testing only. For more information, see
// module.exports ={run, bucketParams};
