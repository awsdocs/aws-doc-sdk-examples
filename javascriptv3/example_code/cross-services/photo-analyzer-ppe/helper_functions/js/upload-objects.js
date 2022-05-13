/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3.

Purpose:
s3_upload_multiple_objects.js uploads multiple files to an Amazon Simple Storage Service (Amazon S3) bucket.

Inputs (replace in code):
- BUCKET_NAME
- PATH/OBJECT_1: Relative path and name of object. For example '../myFiles/index.js'.

Running the code:
node s3_upload_multiple_objects.js

[Outputs | Returns]:
Uploads multiple files to the specified bucket.

*/
// snippet-start:[s3.JavaScript.buckets.uploadMultipleObjectsV3]
// Import required AWS SDK clients and commands for Node.js.
const { PutObjectCommand } = require("@aws-sdk/client-s3");
const { s3Client } = require("../../../ppe/libs/s3Client.js"); // Helper function that creates Amazon S3 service client module.
const path = require("path");
const fs = require("fs");

const BUCKET_NAME = "S3_BUCKET_NAME";
const IMAGES = "OBJECT_PATH_AND_NAMES"; // For example, "../../images/lam.png", "../../images/doctor.png"

const uploadMultipleObjects = async (uploadParams) => {
  try {
    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    console.log("Success. Object(s) updloaded ", data);
  } catch (err) {
    console.log("Error", err);
  }
};
const run = async () => {
  try {
    // Array of relative path and name of object. For example ['images/lam.png', 'image/work', etc.]
    const files = ["OBJECT_PATH_AND_NAMES"];
    for (var i = 0; i < files.length; i++) {
      const file = files[i];
      const fileName = file.substring(file.lastIndexOf("/") + 1, file.length);
      console.log(fileName);
      const fileStream = fs.createReadStream(file);
      console.log(fileStream);
      const uploadParams = {
        Bucket: BUCKET_NAME,
        Key: fileName,
        Body: fileStream,
      };
      uploadMultipleObjects(uploadParams);
    }
  } catch (err) {
    console.log("Error", err);
  }
};

run();
// snippet-end:[s3.JavaScript.buckets.uploadMultipleObjectsV3]
