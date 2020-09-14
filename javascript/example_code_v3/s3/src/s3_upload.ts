/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_upload.ts uploads a file to an S3 bucket.
Inputs (replace in code):
- BUCKET_NAME
- KEY
- BODY
- FILE_NAME
- REGION

Running the code:
ts-node s3_upload.ts

[Outputs | Returns]:
Uploads the specified file to the specified bucket.
*/
// snippet-start:[s3.JavaScript.buckets.uploadV3]
// Import required AWS SDK clients and commands for Node.js
const { S3 } = require("@aws-sdk/client-s3");
const path = require("path");
const fs = require("fs");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const uploadParams = { Bucket: "BUCKET_NAME", Key: "FILE_NAME", Body: "OBJECT_TO_UPLOAD" }; //BUCKET_NAME, KEY (the name of the selected file),
const s3 = new S3(REGION);

// call S3 to retrieve upload file to specified bucket
const run = async () => {
  // Configure the file stream and obtain the upload parameters

  // call S3 to retrieve upload file to specified bucket
  try {
    const data = await s3.putObject(uploadParams);
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.uploadV3]

export {
  run
}
