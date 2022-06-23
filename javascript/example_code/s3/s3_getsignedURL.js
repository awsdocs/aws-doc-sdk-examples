/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS SAMPLE: This sample is part of the Amazon S3 Developer Guide topic at
https://docs.aws.amazon.com/AmazonS3/latest/dev/UploadObjectPreSignedURLJavaScriptSDK.html

Purpose:
s3_getsignedURL.js demonstrates how to generate a presigned URL that a non-authenticated user
can use to retrieve (get) an object to an Amazon S3 bucket.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)
- FILE_NAME (into command line below)
- EXPIRATION (into code; in seconds, e.g., 60*5)

Running the code:
node s3_getsignedURL.js REGION BUCKET_NAME FILE_NAME
*/
// snippet-start:[s3.JavaScript.presignedURL.complete]
const AWS = require('aws-sdk');
// Set the AWS region
const region = process.argv[2]; // REGION
AWS.config.update(region);
// Create S3 service object
const s3 = new AWS.S3();
// Set the parameters
const myBucket = process.argv[3]; //BUCKET_NAME
const myKey = process.argv[4]; // FILE_NAME
const signedUrlExpireSeconds = 60*5; //EXPIRATION

const presignedURL = s3.getSignedUrl('putObject', {
    Bucket: myBucket,
    Key: myKey,
    Expires: signedUrlExpireSeconds
})
console.log(presignedURL)
// snippet-end:[s3.JavaScript.presignedURL.complete]
