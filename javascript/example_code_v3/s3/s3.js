/*
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

This file is licensed under the Apache License, Version 2.0 (the "License").
You may not use this file except in compliance with the License. A copy of
the License is located at http://aws.amazon.com/apache2.0/

This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.

ABOUT THIS NODE.JS SAMPLE: This sample is part of the SDK for JavaScript Developer Guide top
https://docs.aws.amazon.com/cloud9/latest/user-guide/sample-nodejs.html.

Purpose:
s3.js demonstrates how to list, create, and delete a bucket in Amazon S3.

Running the code:
node S3.js BUCKET_NAME REGION

Outputs:
Lists the buckets in the accociated AWS account, then creates a bucket, then deletes it
*/

// snippet-start:[s3.javascript.bucket_operations.list_create_delete]
// To call AWS operations asynchronously.
var async = require('async');

//define parameters for bucket to be created, then deleted
const bucket = process.argv[2];
const region = process.argv[3];
const bucketParams = {
    Bucket: bucket,
    CreateBucketConfiguration: {
        LocationConstraint: region
    }
};

async function listTheseBuckets(myClient){
    try{
        const data = await myClient.listBuckets({});
        console.log('Success', data);
    }
    catch (err){
        console.log('Error', err);
    }
}

async function createThisBucket(myClient){
    try{
        const data = await myClient.createBucket(bucketParams);
        console.log('Success', data);
    }
    catch (err){
        console.log('Error', err);
    }
}

async function deleteThisBucket(myClient){
    try{
        const data = await myClient.deleteBucket(bucketParams);
        console.log('Success, deleted', data);
    }
    catch (err){
        console.log('Error', err);
    }
}
async function run(){
    // Create S3 service object
    const {S3} = require("@aws-sdk/client-s3");
    // To load S3 AWS SDK client for Node.js.
    const s3 = new S3();
    await listTheBuckets({});
    await createTheBucket(s3, bucketParams);
    await deleteTheBucket(s3, bucketParams);
};
run();
// snippet-end:[s3.javascript.bucket_operations.list_create_delete]
exports.listTheseBuckets = listTheseBuckets;
exports.createThisBucket = createThisBucket;
exports.deleteThisBucket = deleteThisBucket;
