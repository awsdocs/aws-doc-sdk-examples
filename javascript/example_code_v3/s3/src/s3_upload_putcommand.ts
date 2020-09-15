/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html.

Purpose:
s3_upload.ts uploads a file to an S3 bucket.

Inputs (in the commandline input below):
- REGION
- BUCKET_NAME
- KEY  The name of the file to upload.
- BODY (in the commandline input below): The contents of the uploaded file. Leave blank/remove to retain contents of original file.

Running the code:
ts-node s3_upload_putcommand.ts
*/
// snippet-start:[s3.JavaScript.buckets.upload_putcommandV3]

// Import required AWS SDK clients and commands for Node.js
const { S3, PutObjectCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Set the parameters
const uploadParams = { Bucket: "BUCKET_NAME", Key: "KEY", Body: "BODY" }; //BUCKET_NAME, KEY (the name of the selected file),
// BODY (the contents of the uploaded file)

// Create S3 service object
const s3 = new S3(REGION);

// call S3 to retrieve upload file to specified bucket
const run = async () => {
  try {
    const data = await s3.send(new PutObjectCommand(uploadParams));
    console.log(
      "Successfully uploaded to " + uploadParams.Bucket + "/" + uploadParams.Key
    );
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.upload_putcommandV3]
//for unit tests only
export = {run};
