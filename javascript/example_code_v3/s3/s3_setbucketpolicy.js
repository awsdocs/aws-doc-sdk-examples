/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-bucket-policies.html

Purpose:
s3_setbucketpolicy.js demonstrates how to set/replace an Amazon S3 bucket policy.

Inputs:
- BUCKET_NAME (in command line input below)
- REGION (in command line input below)

Running the code:
node s3_setbucketpolicy.js BUCKET_NAME REGION
*/
// snippet-start:[s3.JavaScript.policy.putBucketPolicy]
async function run(){
  try{
    const  {S3, PutBucketPolicyCommand}  = require('@aws-sdk/client-s3/');
// create selected bucket resource string for bucket policy
    const bucketResource = "arn:aws:s3:::" + process.argv[2] + "/*";
    const readOnlyAnonUserPolicy = {
      Version: "2012-10-17",
      Statement: [
        {
          Sid: "AddPerm",
          Effect: "Allow",
          Principal: "*",
          Action: [
            "s3:GetObject"
          ],
          Resource: [
            ""
          ]
        }
      ]
    };
    readOnlyAnonUserPolicy.Statement[0].Resource[0] = bucketResource;
// convert policy JSON into string and assign into params
    const bucketPolicyParams = {Bucket: process.argv[2], Policy: JSON.stringify(readOnlyAnonUserPolicy)};
    const region = process.argv[3];
    const s3 = new S3(region);
    const data = await s3.send(new PutBucketPolicyCommand(bucketPolicyParams));
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.policy.putBucketPolicy]
exports.run = run;
