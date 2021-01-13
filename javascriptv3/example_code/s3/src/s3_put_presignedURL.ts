/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_put_presignedURL.ts creates a presigned URL to upload a file to an Amazon Simple Storage Service (Amazon S3) bucket.

Note: This example immediately deletes the object and bucket.

Inputs (replace in code):
- REGION


Running the code:
ts-node s3_put_presignedURL.ts
[Outputs | Returns]:
Uploads the specified file to the specified bucket.
*/

// snippet-start:[s3.JavaScript.buckets.presignedurlv3]
// Import the required AWS SDK clients and commands for Node.js
const {
  S3,
  CreateBucketCommand,
  DeleteObjectCommand,
  PutObjectCommand,
  DeleteBucketCommand,
} = require("@aws-sdk/client-s3");
const { S3RequestPresigner } = require("@aws-sdk/s3-request-presigner");
const { createRequest } = require("@aws-sdk/util-create-request");
const { formatUrl } = require("@aws-sdk/util-format-url");
const fetch = require("node-fetch");

// Set the AWS Region
const REGION = "REGION";

// Set parameters
// Create a random names for the Amazon Simple Storage Service (Amazon S3) bucket and key
const clientParams = {
  Bucket: `test-bucket-${Math.ceil(Math.random() * 10 ** 10)}`,
  Key: `test-object-${Math.ceil(Math.random() * 10 ** 10)}`,
  Body: "BODY"
};

// Create Amazon S3 client object
const s3Client = new S3({ region: REGION });

//Create an S3RequestPresigner object
//To avoid redundant construction parameters when instantiating the Amazon S3 presigner,
// spread the configuration of an existing Amazon S3 client and supply it to the
// presigner's constructor.
const signedRequest = new S3RequestPresigner(s3Client.config);

const run = async () => {
  try {
    //Create an S3 bucket
    console.log(`Creating bucket ${clientParams.Bucket}`);
    await s3Client.send(
        new CreateBucketCommand({ Bucket: clientParams.Bucket })
    );
    console.log(`Waiting for "${clientParams.Bucket}" bucket creation...`);
  } catch (err) {
    console.log("Error creating bucket", err);
  }
  try {
    // Create request
    const request = await createRequest(
        s3Client,
        new PutObjectCommand(clientParams)
    );
    // Create and format presigned URL
    const signedUrl = formatUrl(
        await signedRequest.presign(request, {
          // Supply expiration in second
          expiresIn: 60 * 60 * 24
        })
    );
    console.log(
        `\nPutting "${clientParams.Key}" using signedUrl with body "${clientParams.Body}" in v3`
    );
    console.log(signedUrl);
  } catch (err) {
    console.log("Error creating presigned URL", err);
  }
  try {
    // Delete the object
    console.log(`\nDeleting object "${clientParams.Key}" from bucket`);
    await s3Client.send(new DeleteObjectCommand({Bucket:clientParams.Bucket, Key:clientParams.Key}));
  } catch (err) {
    console.log("Error deleting object", err);
  }
  try {
    // Delete the bucket
    console.log(`\nDeleting bucket ${clientParams.Bucket}`);
    await s3Client.send(new DeleteBucketCommand({Bucket:clientParams.Bucket}));
  } catch (err) {
    console.log("Error deleting bucket", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.presignedurlv3]
