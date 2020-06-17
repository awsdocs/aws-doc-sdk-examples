/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

This sample is part of the SDK for JavaScript Developer Guide topic at

Purpose:
s3_presignedURL.js demonstrates how to manipulate photos in albums stored in an Amazon S3 bucket.

Inputs:
- REGION (in command line input below)
- BUCKET_NAME (in command line input below)
- KEY (in command line input below):i.e. the name of the file to upload
- BODY_TEXT (in command line input below)

Running the code:
node s3_presignedURL.js REGION BUCKET_NAME KEY BODY_TEXT
 */
// snippet-start:[s3.JavaScript.buckets.putPresignedURL]
const AWS = require("aws-sdk");
const { S3, GetObjectCommand } = require("@aws-sdk/client-s3");
const { S3RequestPresigner } = require("@aws-sdk/s3-request-presigner");
const { createRequest } = require("@aws-sdk/util-create-request");
const { formatUrl } = require("@aws-sdk/util-format-url");
const fetch = require("node-fetch");

async function run(){
    let signedUrl;
    let response;
    const region = process.argv[2];
    const Bucket = process.argv[3];
    const Key = process.argv[4];
    const Body = process.argv[5];
    const Expires = 60*5;
    const v2Client = new AWS.S3({ region });
    const v3Client = new S3({ region });
    console.log(`Putting object "${Key}" in bucket`);
    const data =  await v3Client.putObject({ Bucket, Key, Body});
    signedUrl = await v2Client.getSignedUrlPromise("getObject", {
        Bucket,
        Key,
        Expires
    });
    console.log(signedUrl);
};
run();
// snippet-end:[s3.JavaScript.buckets.putPresignedURL]
exports.run = run;
