/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_get_presignedURL.ts demonstrated how to generate a presigned URL to get an object from an
 Amazon Simple Storage Service (Amazon S3) bucket.

Note: This example creates a bucket and object for demonstration purposes, then immediately deletes them.

Inputs (replace in code):
- REGION


Running the code:
ts-node s3_get_presignedURL.ts
[Outputs | Returns]:
Uploads the specified file to the specified bucket.
*/

// snippet-start:[s3.JavaScript.buckets.getpresignedurlv3]
// Import the required AWS SDK clients and commands for Node.js
const {
    S3,
    CreateBucketCommand,
    PutObjectCommand,
    GetObjectCommand,
    DeleteObjectCommand,
    DeleteBucketCommand,
} = require("@aws-sdk/client-s3");
const { S3RequestPresigner } = require("@aws-sdk/s3-request-presigner");
const { createRequest } = require("@aws-sdk/util-create-request");
const { formatUrl } = require("@aws-sdk/util-format-url");
const fetch = require("node-fetch");

// Set parameters
// Create a random names for the Amazon Simple Storage Service (Amazon S3) bucket and key
const params = {
    Bucket: `test-bucket-${Math.ceil(Math.random() * 10 ** 10)}`,
    Key: `test-object-${Math.ceil(Math.random() * 10 ** 10)}`,
    Body: "BODY",
    Region: "REGION"
};

// Create Amazon S3 client object
const s3Client = new S3({ region: params.Region });

// Create an S3RequestPresigner object
const signedRequest = new S3RequestPresigner(s3Client.config);

const run = async () => {
    let signedUrl;
    let response;
    // Create Amazon Simple Storage Service (Amazon S3) bucket
    try {
        console.log(`Creating bucket ${params.Bucket}`);
        const data = await s3Client.send(
            new CreateBucketCommand({ Bucket: params.Bucket })
        );
        console.log(`Waiting for "${params.Bucket}" bucket creation...\n`);
    } catch (err) {
        console.log("Error creating bucket", err);
    }
    // Put object in Amazon S3 bucket
    try {
        console.log(`Putting object "${params.Key}" in bucket`);
        const data = await s3Client.send(
            new PutObjectCommand({ Bucket: params.Bucket, Key: params.Key, Body: params.Body })
        );
    } catch (err) {
        console.log("Error putting object", err);
    }
    // Create presigned URL
    try {
        // Create request
        const request = await createRequest(s3Client, new GetObjectCommand(params));
        // Create and format presigned URL
        const signedUrl = formatUrl(
            await signedRequest.presign(request, {
                // Supply expiration in seconds
                expiresIn: 60 * 60 * 24,
            })
        );
        console.log(
            `\nPutting "${params.Key}" using signedUrl with body "${params.Body}"`
        );
        console.log(signedUrl);
        response = await fetch(signedUrl);
        console.log(
            `\nResponse returned by signed URL: ${await response.text()}\n`
        );
    } catch (err) {
        console.log("Error creating presigned URL", err);
    }
    // Delete object
    try {
        console.log(`\nDeleting object "${params.Key}" from bucket`);
        const data = await s3Client.send(new
            DeleteObjectCommand({ Bucket: params.Bucket, Key: params.Key })
        );
    } catch (err) {
        console.log("Error deleting object", err);
    }
    // Delete bucket
    try {
        console.log(`\nDeleting bucket ${params.Bucket}`);
        const data = await s3Client.send(new
            DeleteBucketCommand({ Bucket: params.Bucket, Key: params.Key })
        );
    } catch (err) {
        console.log("Error deleting object", err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.getpresignedurlv3]
