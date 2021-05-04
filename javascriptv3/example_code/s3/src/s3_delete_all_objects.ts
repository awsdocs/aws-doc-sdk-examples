/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_delete_all_objectS.ts demonstrates how to all objects from an Amazon Simple Storage Solution (S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME

Running the code:
ts-node s3_delete_all_objectS.ts
*/
// snippet-start:[s3.JavaScript.buckets.deleteoallbjectsV3]
const { S3Client, ListObjectsCommand, DeleteObjectCommand } = require("@aws-sdk/client-s3");

const REGION = "REGION"; //e.g. "us-east-1"

const params = { Bucket: "BUCKET_NAME" };

// Create Amazon S3 client service object.
const s3 = new S3Client({ region: REGION });

const run = async () => {
    try {
        const data = await s3.send(new ListObjectsCommand(params));
        let i =0;
        let noOfObjects = data.Contents
        for (let i = 0; i < noOfObjects.length; i++) {
            const data = await s3.send(
                new DeleteObjectCommand({
                    Bucket: params.Bucket,
                    Key: noOfObjects[i].Key
                })
            );
        }
        console.log("Success. Objects deleted.");
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.deleteoallbjectsV3]


