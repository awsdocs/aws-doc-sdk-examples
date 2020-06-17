/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
// https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-configuring-buckets.html

Purpose:
s3_getcors.js demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.

Inputs (in command line input below):
- REGION
- BUCKET_NAME

Running the code:
node s3_getcors.js REGION BUCKET_NAME
 */
// snippet-start:[s3.JavaScript.cors.getBucketCors]
async function run(){
  try{
    const { S3, GetBucketCorsCommand } = require("@aws-sdk/client-s3");
    const region = process.argv[2];
    const s3 = new S3(region);
    const bucket = process.argv[3];
    const bucketParams = {Bucket: bucket};
    const data = await s3.send(new GetBucketCorsCommand(bucketParams));
    console.log('Success', JSON.stringify(data.CORSRules));
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.cors.getBucketCors]
//for unit testing only
exports.run = run;

