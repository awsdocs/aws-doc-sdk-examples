/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

//snippet-sourcedescription:[s3_presignedURL.js demonstrates how to manipulate photos in albums stored in an Amazon S3 bucket.]
//snippet-service:[s3]
//snippet-keyword:[JavaScript]
//snippet-sourcesyntax:[javascript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2021-04-21]
//snippet-sourceauthor:[AWS-JSDG]

// ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic at
// https://docs.aws.amazon.com/AmazonS3/latest/dev/

// snippet-start:[s3.JavaScript.presignedURL.complete]

const AWS = require('aws-sdk');

// Create S3 service object
const s3 = new AWS.S3();

// Set the region 
AWS.config.update({region: 'REGION'});

// **DO THIS**:
//   Replace BUCKET_NAME with the name of the bucket,  FILE_NAME with the name of the file you want to upload (including relative page), and EXPIRATION with the duration in validaty in seconds (e.g 60 *5)
const myBucket = 'BUCKET_NAME'
const myKey = 'FILE_NAME'
const signedUrlExpireSeconds = EXPIRATION

const presignedURL = s3.getSignedUrl('putObject', {
Bucket: myBucket,
Key: myKey,
Expires: signedUrlExpireSeconds
})

console.log(presignedURL)

//<!-- snippet-end:[s3.JavaScript.presignedURL.complete] -->