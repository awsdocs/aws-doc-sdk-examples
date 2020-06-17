/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-static-web-host.html

Purpose:
This function applies a bucket website configuration to a selected bucket, and is part of an exmample of using an Amazon S3 bucket as a static web host, at

Inputs:
- REGION (in command line input below)
- BUCKET_NAME (in command line input below): the name of the bucket, which you enter to run the code (see 'Running the code' below).
- INDEX_PAGE (in command line input below): the index document inserted into params JSON, which you enter to run the code (see 'Running the code' below).
- ERROR_PAGE (in command line input below): the error document inserted into params JSON, which you enter to run the code (see 'Running the code' below).

Running the code:
node s3_setbucketwebsite.js REGION BUCKET_NAME INDEX_PAGE ERROR_PAGE
 */
// snippet-start:[s3.JavaScript.website.putBucketWebsite]
async function run(){
  try{
const staticHostParams = {
  Bucket: '',
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: ''
    },
    IndexDocument: {
      Suffix: ''
    },
  }
};
// Insert specified bucket name and index and error documents into params JSON
// from command line arguments
    const  {S3, PutBucketWebsiteCommand}  = require('@aws-sdk/client-s3/');
    const region = process.argv[2];
    const s3 = new S3();
    staticHostParams.Bucket = process.argv[3];
    staticHostParams.WebsiteConfiguration.IndexDocument.Suffix = process.argv[4];
    staticHostParams.WebsiteConfiguration.ErrorDocument.Key = process.argv[5];
// set the new website configuration on the selected bucket
    const data = await s3.send(new PutBucketWebsiteCommand(staticHostParams));
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.website.putBucketWebsite]
exports.run = run;


