/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/using-lambda-s3-setup.html.

Purpose:
    s3-bucket-setup.js demonstrates how to create an Amazon S3 bucket.

    Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)

Running the code:
    node s3-bucket-setup.js REGION BUCKET_NAME
*/
// snippet-start:[lambda.JavaScript.v3.BucketSetUp.NonModular]
// Import a non-modular S3 client
const { S3 } = require('@aws-sdk/client-s3');
// Instantiate the S3 client
const s3 = new S3({region: 'us-west-2'});

// Create params JSON for S3.createBucket
const bucketParams = {
  Bucket: process.argv[2],
  ACL: 'public-read'
};

// Create params JSON for S3.setBucketWebsite
const staticHostParams = {
  Bucket: process.argv[2],
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: 'error.html'
    },
    IndexDocument: {
      Suffix: 'index.html'
    },
  }
};

// call S3 to create the bucket
s3.createBucket(bucketParams, function (err, data) {
  if (err) {
    console.log("Error", err);
  } else {
    console.log("Bucket URL is ", data.Location);
    const putWebsiteOn = s3.putBucketWebsite(staticHostParams).promise();
    putWebsiteOn.then(function (data) {
      // update the displayed policy for the selected bucket
      console.log("Success", data);
    }).catch(function (err) {
      console.log(err);
    });
  }
});
// snippet-end:[lambda.JavaScript.v3.BucketSetUp.NonModular]
