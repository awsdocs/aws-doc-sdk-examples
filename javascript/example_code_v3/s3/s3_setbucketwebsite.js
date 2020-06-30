/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS JavaScript SDK,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-static-web-host.html.

Purpose:
This function applies a bucket website configuration to a selected bucket, and is part of an exmample
of using an Amazon S3 bucket as a static web host.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below): the name of the bucket, which you enter to run the code (see 'Running the code' below).
- INDEX_PAGE (into command line below): the index document inserted into params JSON, which you enter to run the code (see 'Running the code' below).
- ERROR_PAGE (into command line below): the error document inserted into params JSON, which you enter to run the code (see 'Running the code' below).

Running the code:
node s3_setbucketwebsite.js REGION BUCKET_NAME INDEX_PAGE ERROR_PAGE
 */
// snippet-start:[s3.JavaScript.v3.website.putBucketWebsite]
async function run(){
    // Import required AWS SDK clients and commands for Node.js
    const  {S3, PutBucketWebsiteCommand}  = require('@aws-sdk/client-s3/');
    const region = process.argv[2];
    const s3 = new S3(region);
    // Create the parameters
    const staticHostParams = {
        Bucket: '',
        WebsiteConfiguration: {
            ErrorDocument: {
                Key: ''
            },
            IndexDocument: {
                Suffix: ''
            },
        }
    };
    // Insert specified bucket name and index and error documents into params JSON
    // from command line arguments
    staticHostParams.Bucket = process.argv[3];
    staticHostParams.WebsiteConfiguration.IndexDocument.Suffix = process.argv[4];
    staticHostParams.WebsiteConfiguration.ErrorDocument.Key = process.argv[5];
    // set the new website configuration on the selected bucket
  try{
    const data = await s3.send(new PutBucketWebsiteCommand(staticHostParams));
    console.log('Success', data);
  }
  catch (err){
    console.log('Error', err);
  }
};
run();
// snippet-end:[s3.JavaScript.v3.website.putBucketWebsite]
//for unit tests only
exports.run = run;


