/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
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
const { S3Client, PutObjectCommand } = require("@aws-sdk/client-s3");
const path = require("path");
const fs = require("fs");

// Set the AWS region
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
const uploadParams = { Bucket: "BUCKET_NAME", Key: "KEY", Body: "BODY" }; //BUCKET_NAME, KEY (the name of the selected file),
// BODY (the contents of the uploaded file - leave blank/remove to retain contents of original file.)
const file = "FILE_NAME"; //FILE_NAME (the name of the file to upload (if you don't specify KEY))

// Create S3 service object
const s3 = new S3Client({ region: REGION });

// call S3 to retrieve upload file to specified bucket
const run = async () => {
  // Configure the file stream and obtain the upload parameters
  const fileStream = fs.createReadStream(file);
  fileStream.on("error", function (err) {
    console.log("File Error", err);
  });
  uploadParams.Key = path.basename(file);
  // call S3 to retrieve upload file to specified bucket
  try {
    const data = await s3.send(new PutObjectCommand(uploadParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.uploadV3]

