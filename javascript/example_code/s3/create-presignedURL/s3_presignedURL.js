/**
 * Copyright 2010-2020 Amazon.com
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

/* ABOUT THIS NODE.JS SAMPLE: 
Purpose:
  This function generates a pre-signed URL that enables anyone to upload the specified key (filename) to the specified bucket.
  This sample is part of the SDK for JavaScript Developer Guide topic at
  https://docs.aws.amazon.com/AmazonS3/latest/dev/PresignedUrlUploadObject.html

Inputs:
  - bucketName: the name of the bucket (e.g. test-bucket)
  - objectKey: the name and relative path to the file to upload
  - expiry: Duration until pre-signed URL expires in seconds (e.g. 60*5)

[Outputs | Returns]:
  Returns pre-signed URL in console.log.

*/

// snippet-start:[s3.JavaScript.presignedURL.complete]

const S3 = require('aws-sdk/clients/s3');
const s3 = new S3({apiVersion: '2006-03-01'});

const  urlParams = {
  Bucket: "bucketName",
  Key: "objectKey",
  Expires: expiry
}

generatePresignedURL = async (bucketName, objectKey, expiry) =>{

  const preSignedURL =  await s3.getSignedUrl("putObject", urlParams);
  console.log("This the the pre-signed URL:" + preSignedURL);

};
generatePresignedURL()

//<!-- snippet-end:[s3.JavaScript.presignedURL.complete] -->
exports.generatePresignedURL = generatePresignedURL
exports.urlParams = urlParams
