/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

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