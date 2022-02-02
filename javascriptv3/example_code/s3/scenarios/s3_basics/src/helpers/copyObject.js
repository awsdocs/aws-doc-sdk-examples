/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
user guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html

Purpose:
Helper function that copies an object from the root of an Amazon S3 bucket to a directory in that bucket.
 */

import { CopyObjectCommand } from "@aws-sdk/client-s3";
import { s3Client, REGION } from "../../libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.
import { bucket_name, object_key } from "../s3_basics.js"; // Import constants from s3_basics.js.
import { deleteResources } from "./deleteResources.js";

export const copyObject = async () => {
  try {
    console.log(
      "\nCopying object named " +
        object_key +
        " to bucket " +
        bucket_name +
        "/" +
        object_key +
        " ...\n"
    );

    // Set the parameters for copying an object to a directory.
    const copy_object_params = {
      Bucket: bucket_name,
      CopySource: "/" + bucket_name + "/" + object_key,
      Key: "copy-destination/" + object_key,
    };
    const data = await s3Client.send(new CopyObjectCommand(copy_object_params));
    console.log("Success, object copied to folder.");
    try {
      deleteResources();
    } catch (err) {
      console.log("An error occurred copying the object", err);
    }
    return data; // For unit tests.
  } catch (err) {
    console.log("Error copying object from to folder", err);
  }
};
