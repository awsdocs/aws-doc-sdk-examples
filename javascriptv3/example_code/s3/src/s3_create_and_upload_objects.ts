/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_create_and_upload_objects.ts creates and uploads an object to an Amazon Simple Storage Solution (Amazon S3) bucket.
Inputs (in the commandline input below):
- REGION
- BUCKET_NAME
- KEY: The name of the object to create and upload.
- BODY: The contents of the uploaded file.

Running the code:
ts-node s3_create_and_upload_object.ts
*/
// snippet-start:[s3.JavaScript.buckets.upload_putcommandV3]

// Import required AWS SDK clients and commands for Node.js
const { S3Client, PutObjectCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters.
const uploadParams = {
  Bucket: "BUCKET_NAME",
  // Specify the name of the new object. For example, 'index.html'.
  // To create a directory for the object, use '/'. For example, 'myApp/package.json'.
  Key: "OBJECT_NAME",
  // Content of the new object.
  Body: "BODY"
};

// Create Amazon S3 service client object.
const s3 = new S3Client({ region: REGION });

// Create and upload the object to the specified Amazon S3 bucket.
const run = async () => {
  try {
    const data = await s3.send(new PutObjectCommand(uploadParams));
    console.log(
        "Successfully uploaded object: " + uploadParams.Bucket + "/" + uploadParams.Key
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[s3.JavaScript.buckets.upload_putcommandV3]

