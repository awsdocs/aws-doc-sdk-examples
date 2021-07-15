/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_copy_objects.js demonstrates how to copy an object from one Amazon Simple Storage Solutions (Amazon S3) bucket to another.

Inputs (replace in code):
- BUCKET_NAME

Running the code:
node s3_copy_objects.js
*/
// snippet-start:[s3.JavaScript.buckets.copyObjectV3]
// Get service clients module and commands using ES6 syntax.
import { CopyObjectCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js";

// Set the bucket parameters.
const params = { Bucket: "brmurbucket",
    CopySource: "/setupstackrekognition-rekognitiondemobucketcf294c-1nyd75ojc41xf/car_merc.png",
    Key: "car_merc.png" };

// Create the Amazon S3 bucket.
const run = async () => {
    try {
        const data = await s3Client.send(new CopyObjectCommand(bucketParams));
        console.log("Success", data.Location);
        return data; // For unit tests.
    } catch (err) {
        console.log("Error", err);
    }
};
run();
// snippet-end:[s3.JavaScript.buckets.copyObjectV3]
// For unit tests only.
// module.exports ={run, bucketParams};
