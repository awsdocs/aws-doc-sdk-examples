/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-static-web-host.html

Purpose:
s3_getbucketwebsite.js demonstrates how to retrieve the website configuration of an Amazon S3 bucket.

Inputs (in command line input below):
- REGION
- BUCKET_NAME

Running the code:
node S3.js  REGION BUCKET_NAME

// snippet-start:[s3.JavaScript.website.getBucketWebsite]
 */
async function run(){
  try{
    const { S3, GetBucketWebsiteCommand } = require("@aws-sdk/client-s3");
    const region = process.argv[2];
    const bucketParams = {Bucket: process.argv[3]};
    const s3 = new S3();
    const data = await s3.send(new GetBucketWebsiteCommand(bucketParams));
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.website.getBucketWebsite]
exports.run = run;
