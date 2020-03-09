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

// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Load credentials and set region from JSON file
AWS.config.loadFromPath('./config.json');

// Create S3 service object
s3 = new AWS.S3({apiVersion: '2006-03-01'});

// Create params JSON for S3.createBucket
var bucketParams = {
  Bucket : process.argv[2],
  ACL : 'public-read'
};

// Create params JSON for S3.setBucketWebsite
var staticHostParams = {
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
s3.createBucket(bucketParams, function(err, data) {
  if (err) {
    console.log("Error", err);
  } else {
    console.log("Bucket URL is ", data.Location);
    // set the new policy on the cewly created bucket
    s3.putBucketWebsite(staticHostParams, function(err, data) {
      if (err) {
        // display error message
        console.log("Error", err);
      } else {
        // update the displayed policy for the selected bucket
        console.log("Success", data);
      }
    });
  }
});
