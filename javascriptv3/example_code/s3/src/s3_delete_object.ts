/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_delete_object.ts demonstrates how to delete an object from an Amazon Simple Storage Solution (S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- KEY

Running the code:
ts-node s3_delete_object.ts
*/
// snippet-start:[s3.JavaScript.buckets.deleteobjectV3]
const { S3Client, DeleteObjectCommand } = require("@aws-sdk/client-s3");

const REGION = "REGION"; //e.g. "us-east-1"

const params = { Bucket: "BUCKET_NAME", Key: "KEY" };

// Create Amazon S3 client service object.
const s3 = new S3Client({ region: REGION });


const run = async () => {
    try {
        const data = await s3.send(new DeleteObjectCommand(params));
        console.log("Success. Object deleted.", data);
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.deleteobjectV3]


