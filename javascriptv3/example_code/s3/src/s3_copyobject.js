/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_copyobject.js demonstrates how to copy an object from one Amazon Simple Storage Solution (Amazon S3) bucket to another.

Inputs (replace in code):
- DESTINATION_BUCKET_NAME
- SOURCE_BUCKET_NAME
- OBJECT_NAME

Running the code:
node s3_copyobject.js
*/
// snippet-start:[s3.JavaScript.buckets.copyObjectV3]
// Get service clients module and commands using ES6 syntax.
import { CopyObjectCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js";

// Set the bucket parameters.

export const params = {
    Bucket: "DESTINATION_BUCKET_NAME",
    CopySource: "/SOURCE_BUCKET_NAME/OBJECT_NAME",
    Key: "OBJECT_NAME"
};

// Create the Amazon S3 bucket.
export const run = async () => {
    try {
        const data = await s3Client.send(new CopyObjectCommand(params));
        console.log("Success", data);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.copyObjectV3]
// For unit tests only.
//exports {run, bucketParams};
