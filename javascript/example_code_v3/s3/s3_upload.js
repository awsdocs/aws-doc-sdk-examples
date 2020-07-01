/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_upload.js uploads a file to an S3 bucket.
Inputs:
- BUCKET_NAME (into command line below)
- KEY (into command line below): (optional)The Key parameter is set to the name of the selected file (existing or not), which you enter to run the code (see 'Running the code' below).
- BODY (into command line below): (optional)The Body parameter is the contents of the uploaded file. Leave blank/remove to retain contents of original file.
- FILE_NAME (into command line below): Name of the file to upload (if you don't specify KEY)
- REGION

Running the code:
node s3_upload.js REGION BUCKET_NAME KEY BODY FILE_NAME

[Outputs | Returns]:
Uploads the specified file to the specified bucket.
*/
// snippet-start:[s3.JavaScript.v3.buckets.upload]
async function run() {
  const path = require('path');
  const fs = require('fs');

  // Import required AWS SDK clients and commands for Node.js
  const { S3 } = require("@aws-sdk/client-s3");
  // Create S3 service object
  const region = process.argv[2];
  const s3 = new S3(region);

// call S3 to retrieve upload file to specified bucket
  const uploadParams = {Bucket: process.argv[3], Key: process.argv[4], Body:process.argv[5]};
  const file = process.argv[6];
// Configure the file stream and obtain the upload parameters
  const fileStream = fs.createReadStream(file);
  fileStream.on('error', function(err) {
    console.log('File Error', err);
  });
  uploadParams.Key = path.basename(file);
// call S3 to retrieve upload file to specified bucket
  try {
    const data = await s3.putObject(uploadParams);
    console.log('Success', data);
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.v3.buckets.upload]
exports.run = run; //for unit tests only
