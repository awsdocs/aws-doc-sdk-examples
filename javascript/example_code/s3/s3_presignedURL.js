/**
 * Copyright Amazon.com
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

const S3 = require("aws-sdk/clients/s3");
const region = "REGION";
const s3 = new S3({ region });

// **DO THIS**:
//   Replace BUCKET_NAME with the name of the bucket,  FILE_NAME with the name of the file to upload (including relative page).
const Bucket = "BUCKET_NAME";
const Key = "FILE_NAME";
// **DO THIS**:
// Adjust duration of validity in seconds, as required(e.g., 60 *5)
const Expires = 60 * 5;
const urlParams = {
  Bucket: Bucket,
  Key: Key,
  Expires: Expires,
};
generatePresignedURL = () => {
  const preSignedURL = s3.getSignedUrl("putObject", urlParams);
  console.log("This the the pre-signed URL:" + preSignedURL);
};
generatePresignedURL();
//<!-- snippet-end:[s3.JavaScript.presignedURL.complete] -->
exports.generatePresignedURL = generatePresignedURL;
