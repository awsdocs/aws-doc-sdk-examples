/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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

//snippet-sourcedescription:[s3_createbucket.js demonstrates how to create an Amazon S3 bucket.]
//snippet-service:[s3]
//snippet-keyword:[JavaScript]
//snippet-sourcesyntax:[javascript]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-06-02]
//snippet-sourceauthor:[AWS-JSDG]

/* ABOUT THIS NODE.JS SAMPLE:
Purpose:
This function creates an s3 bucket.
This sample is part of the SDK for JavaScript Developer Guide topic at
https://docs.aws.amazon.com/sdk-for-javascript/v2/developer-guide/s3-example-creating-buckets.html.
Inputs:
- bucketName: the name of the bucket (e.g. test-bucket)
Note: Enter the same name in both instances of bucketName

[Outputs | Returns]:
Returns presigned URL in console.log.
*/
// snippet-start:[s3.JavaScript.buckets.createBucket]
// Load the AWS SDK for Node.js
const  {S3}  = require('@aws-sdk/client-s3/');

// Create S3 service object
const s3 = new S3();

// Create the parameters for calling createBucket
const bucketParams = {
  Bucket : "bucketname"
};

// call S3 to create the bucket
async function createFunction(bucketParams){
  try {
    const bucketParams = {
      Bucket : "bucketname"
    };
    const response =  await s3.createBucket(bucketParams);
  }
  catch (err) {
    console.log('Error', err);
  }
};
createFunction();
// snippet-end:[s3.JavaScript.buckets.createBucket]
exports.createFunction = createFunction;
exports.bucketParams = bucketParams;
exports.s3 = s3;

