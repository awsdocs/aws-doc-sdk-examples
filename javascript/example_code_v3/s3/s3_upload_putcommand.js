/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/getting-started-nodejs.html.

Purpose:
s3_upload.js uploads a file to an S3 bucket.

Inputs (in the commandline input below):
- REGION (in the commandline input below)
- BUCKET_NAME (in the commandline input below)
- KEY (in the commandline input below): The name of the file to upload.
- BODY (in the commandline input below): The contents of the uploaded file. Leave blank/remove to retain contents of original file.

Running the code:
node s3_upload_putcommand.js REGION BUCKET_NAME KEY BODY
*/
// snippet-start:[s3.JavaScript.buckets.upload_putcommand]
async function run() {
// call S3 to retrieve upload file to specified bucket
    try {
        const { S3, PutObjectCommand } = require("@aws-sdk/client-s3");
        const region = process.argv[2];
        const s3 = new S3(region);
        const uploadParams = {Bucket: process.argv[3], Key: process.argv[4], Body:process.argv[5]};
        const data = await s3.send(new PutObjectCommand(uploadParams));
        console.log('Successfully uploaded to ' + uploadParams.Bucket +'/'+ uploadParams.Key);
    }
    catch (err) {
        console.log('Error', err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.upload]
exports.run = run;
