/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_listbuckets.js demonstrates how to list all the buckets in an AWS account.

Inputs:
- REGION (in command line input below)
- BUCKET_NAME (in command line input below)

Running the code:
node s3_listobjects.js REGION
*/
// snippet-start:[s3.JavaScript.buckets.listBuckets]
async function run() {
  try {
    const { S3 } = require("@aws-sdk/client-s3");
    const region = process.argv[2];
    const s3 = new S3();
    const data = await s3.listBuckets();
    console.log('Success', data.Buckets);
  }
  catch (err) {
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.listBuckets]
exports.run = run;

