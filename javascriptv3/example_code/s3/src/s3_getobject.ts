/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_getobject.ts gets an object from an Amazon Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- OBJECT

Running the code:
ts-node s3_getobject.ts

[Outputs | Returns]:
Returns the object from the Amazon S3 bucket.
*/
// snippet-start:[s3.JavaScript.buckets.getobjectV3]
// Import required AWS SDK clients and commands for Node.js
const { S3Client, GetObjectCommand } = require("@aws-sdk/client-s3");

// Set the AWS region.
const REGION = "REGION"; //e.g. "us-east-1"

// Set the parameters
// Specify the name of the bucket and the object to return.
const params = { Bucket: "BUCKET_NAME", Key: "OBJECT" };

// Create an Amazon S3 client service object.
const s3 = new S3Client({ region: REGION });

const run = async () => {
    try {
        const data = await s3.send(new GetObjectCommand(params));
        console.log("Success, bucket returned", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.getobjectV3]
