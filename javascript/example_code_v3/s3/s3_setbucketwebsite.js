/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-static-web-host.html.

Purpose:
This function applies a bucket website configuration to a selected bucket, and is part of an exmample
of using an Amazon S3 bucket as a static web host.

Inputs (replace in code):
- INDEX_PAGE
- ERROR_PAGE

Running the code:
node s3_setbucketwebsite.js
 */
// snippet-start:[s3.JavaScript.website.putBucketWebsiteV3]

// Import required AWS SDK clients and commands for Node.js
const { S3, PutBucketWebsiteCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "region"; //e.g. "us-east-1"

// Create the parameters for the bucket
const bucketParams = { Bucket: "BUCKET_NAME" };
const staticHostParams = {
  Bucket: bucketParams,
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: "",
    },
    IndexDocument: {
      Suffix: "",
    },
  },
};

// Create S3 service object
const s3 = new S3(REGION);

const run = async () => {
  // Insert specified bucket name and index and error documents into params JSON
  // from command line arguments
  staticHostParams.Bucket = bucketParams;
  staticHostParams.WebsiteConfiguration.IndexDocument.Suffix = "INDEX_PAGE"; // the index document inserted into params JSON
  staticHostParams.WebsiteConfiguration.ErrorDocument.Key = "ERROR_PAGE"; // : the error document inserted into params JSON
  // set the new website configuration on the selected bucket
  try {
    const data = await s3.send(new PutBucketWebsiteCommand(staticHostParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.website.putBucketWebsiteV3]
//for unit tests only
exports.run = run;
