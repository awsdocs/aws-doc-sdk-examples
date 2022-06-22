/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS SAMPLE: This sample is part of the Amazon S3 Developer Guide topic at
https://docs.aws.amazon.com/AmazonS3/latest/dev/UploadObjectPreSignedURLJavaScriptSDK.html

Purpose:
s3_put_presignedURL.js demonstrates how to generate a presigned URL that a non-authenticated user
can use to upload (put) an object to an Amazon S3 bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- FILE_NAME
- EXPIRATION: in seconds, e.g., 60*5

Running the code:
node s3_presignedURLs.js
*/
// snippet-start:[s3.JavaScript.buckets.presignedURL.complete]
const AWS = require('aws-sdk');
// Set the AWS region
const region = "REGION"; // REGION
AWS.config.update(region);
// Create S3 service object
const s3 = new AWS.S3();
// Set the parameters
const myBucket = "BUCKET_NAME"; //BUCKET_NAME
const myKey = "FILE_NAME"; // FILE_NAME
const signedUrlExpireSeconds = 60*5; //EXPIRATION

const presignedURL = s3.getSignedUrl('putObject', {
    Bucket: myBucket,
    Key: myKey,
    Expires: signedUrlExpireSeconds
})
console.log(presignedURL)
// snippet-end:[s3.JavaScript.buckets.presignedURL.complete]
