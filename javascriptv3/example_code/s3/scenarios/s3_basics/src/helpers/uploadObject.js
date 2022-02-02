/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
user guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html

Purpose:
Helper function that creates and uploads object to an Amazon S3 bucket.
 */

import { PutObjectCommand } from "@aws-sdk/client-s3";
import { s3Client, REGION } from "../../libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.
import { bucket_name, object_key, object_content } from "../s3_basics.js"; // Import constants from s3_basics.js.
import { copyObject } from "./copyObject.js";

export const uploadObject = async () => {

  // Create constants from the constants imported from s3_basics.js.
  const object_upload_params = {
    Bucket: bucket_name,
    // Specify the name of the new object. For example, 'test.html'.
    // To create a directory for the object, use '/'. For example, 'myApp/package.json'.
    Key: object_key,
    // Content of the new object.
    Body: object_content
  };
  try {
    console.log(
      "\nCreating and uploading object named " +
        object_key +
        " to first bucket " +
        bucket_name +
        " ...\n"
    );
    const data = await s3Client.send(
      new PutObjectCommand(object_upload_params)
    );
    console.log(
      "Successfully uploaded object: " +
        object_upload_params.Bucket +
        "/" +
        object_upload_params.Key
    );
    try {
      copyObject();
    } catch (err) {
      console.log("Error copying object", err);
    }
    return data // For unit tests.
  } catch (err) {
    console.log("Error creating and upload object to  bucket", err);
  }
};
