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

// Import an S3 client
const { 
  S3Client, CreateBucketCommand, PutBucketWebsiteCommand 
} = require('@aws-sdk/client-s3');
// Instantiate an S3 client
const s3 = new S3Client({region: 'us-west-2'});

// Create params JSON for S3.createBucket
const bucketParams = {
  Bucket : process.argv[2],
  ACL : 'public-read'
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

async function run() {
  try {
    // call S3 to create the bucket
    const response = await s3.send(new CreateBucketCommand(bucketParams));
    console.log('Bucket URL is ', response.Location);
  } catch(err) {
    console.log('Error', err);
  }
  try {
    // set the new policy on the cewly created bucket
    const response = await s3.send(new PutBucketWebsiteCommand(staticHostParams));
    // update the displayed policy for the selected bucket
    console.log('Success', response);
  } catch(err) {
    // display error message
    console.log('Error', err);
  }
}

run();
