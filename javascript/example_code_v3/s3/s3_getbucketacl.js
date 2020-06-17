/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide topic
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-access-permissions.html

Purpose:
s3_getbucketacl.js demonstrates how to retrieve the access control list of an Amazon S3 bucket.

Inputs:
- REGION (in command line input below)
- BUCKET_NAME (in command line input below)

Running the code:
node S3.js  REGION BUCKET_NAME

Outputs:
Lists the buckets in the associated AWS account, then creates a bucket, then deletes it

snippet-start:[s3.JavaScript.perms.getBucketAcl]
 */
async function run(){
  try{
    const  {S3, GetBucketAclCommand}  = require('@aws-sdk/client-s3/');
    const region = process.argv[2];
    const bucketParams = {Bucket: process.argv[3]};
    const s3 = new S3(region);
    const data = await s3.send(new GetBucketAclCommand(bucketParams));
    console.log('Success', data.Grants);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.perms.getBucketAcl]
exports.run = run;
