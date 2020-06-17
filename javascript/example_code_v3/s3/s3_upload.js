/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_upload.js uploads a file to an S3 bucket.
Inputs:
- BUCKET_NAME (in command line input below): the name of the bucket, which you enter to run the code (see 'Running the code' below).
- KEY (in command line input below): (optional)The Key parameter is set to the name of the selected file (existing or not), which you enter to run the code (see 'Running the code' below).
- BODY (in command line input below): (optional)The Body parameter is the contents of the uploaded file. Leave blank/remove to retain contents of original file.
- FILE_NAME (in command line input below): Name of the file to upload (if you don't specify KEY)
- REGION

Running the code:
node s3_upload.js BUCKET_NAME KEY BODY FILE_NAME REGION

[Outputs | Returns]:
Uploads the specified file to the specified bucket.
*/
// snippet-start:[s3.JavaScript.buckets.upload]
async function run() {
  const path = require('path');
  const fs = require('fs');
// call S3 to retrieve upload file to specified bucket
  const uploadParams = {Bucket: process.argv[2], Key: process.argv[3], Body:process.argv[4]};
  const file = process.argv[5];
// Configure the file stream and obtain the upload parameters
  const fileStream = fs.createReadStream(file);
  fileStream.on('error', function(err) {
    console.log('File Error', err);
  });
  uploadParams.Key = path.basename(file);
// call S3 to retrieve upload file to specified bucket
  try {
    // Load the AWS SDK s3 client for Node.js
    const { S3 } = require("@aws-sdk/client-s3");
    // Create S3 service object
    const region = process.argv[6];
    const s3 = new S3(region);
    const data = await s3.putObject(uploadParams);
    console.log('Success', data);
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.upload]
exports.run = run;
