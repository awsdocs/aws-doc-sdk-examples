/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with Version 3 (V3) of the AWS SDK for JavaScript,
which is scheduled for release later in 2020. The prerelease version of the SDK is available
at https://github.com/aws/aws-sdk-js-v3. The 'SDK for JavaScript Developer Guide' for V3 is also
scheduled for release later in 2020, and the topic containing this example will be hosted at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/getting-started-nodejs.html.

Purpose:
sample.js demonstrates how to get started using node.js with the AWS SDK for JavaScript.

Inputs (replace in code):
 - REGION
 - BUCKET_NAME

Running the code:
node sample.js

// snippet-start:[GettingStarted.JavaScript.NodeJS.getStarted]

// Import required AWS SDK clients and commands for Node.js
 */
const { S3, PutObjectCommand } = require("@aws-sdk/client-s3");

// Set the AWS region
const REGION = "eu-west-1"; // e.g., "us-east-1"

// Set the bucket parameters
const bucketName = "BUCKET_NAME";
const bucketParams= {Bucket: bucketName};

// Create name for uploaded object key
const keyName = 'hello_world.txt';
const objectParams = {Bucket: bucketName, Key: keyName, Body: 'Hello World!'};

// Create an S3 client service object
const s3 = new S3(REGION);

const run = async ()=> {
    // Create S3 bucket
    try{
    const data = await s3.createBucket(bucketParams);
    console.log('Success. Bucket created.');
    }
    catch(err){
        console.log('Error', err);
    }
   try{
        const results = await s3.send(new PutObjectCommand(objectParams));
        console.log("Successfully uploaded data to " + bucketName + "/" + keyName);
    }
    catch(err){
        console.log('Error', err);
    }
};
run();
// snippet-end:[GettingStarted.JavaScript.NodeJS.getStarted]
//for unit tests only
exports.run = run();
