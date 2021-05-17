/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_putobject.js uploads an existing file to an Amazon Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- REGION
- BUCKET_NAME
- OBJECT_PATH_AND_NAME: Relative path and name of object. For example '../myFiles/index.js'.

Running the code:
nodes3_putobject.js

[Outputs | Returns]:
Uploads the specified file to the specified bucket.

*/
// snippet-start:[s3.JavaScript.buckets.uploadV3]
// Import required AWS SDK clients and commands for Node.js.
import { PutObjectCommand } from "@aws-sdk/client-s3";
 import { s3 } from "./libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.
import {path} from "path";
import {fs} from "fs";

// Set the AWS Region.
const REGION = "REGION"; //e.g. "us-east-1"

const file = "OBJECT_PATH_AND_NAME"; // Path to and name of object. For example '../myFiles/index.js'.
const fileStream = fs.createReadStream(file);

// Set the parameters
const uploadParams = {
  Bucket: "BUCKET_NAME",
  // Add the required 'Key' parameter using the 'path' module.
  Key: path.basename(file),
  // Add the required 'Body' parameter
  Body: fileStream,
};


// Upload file to specified bucket.
const run = async () => {
  try {
    const data = await s3.send(new PutObjectCommand(uploadParams));
    console.log("Success", data);
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();

// snippet-end:[s3.JavaScript.buckets.uploadV3]
// For unit testing only.
// module.exports ={run, uploadParams};
