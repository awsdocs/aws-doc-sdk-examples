/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
user guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html

Purpose:
Helper function that deletes an Amazon S3 bucket and the objects and directory in that bucket.
 */

import { DeleteBucketCommand, DeleteObjectCommand } from "@aws-sdk/client-s3";
import { s3Client } from "../../libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.
import { bucket_name, object_key } from "../s3_basics.js"; // Import constants from s3_basics.js.

export const deleteResources = async () => {
  try {
    console.log("\nDeleting " + object_key + " from" + bucket_name);
    const delete_object_from_bucket_params = {
      Bucket: bucket_name,
      Key: object_key,
    };

    await s3Client.send(
      new DeleteObjectCommand(delete_object_from_bucket_params)
    );
    console.log("Success. Object deleted from bucket.");
    try {
      console.log(
        "\nDeleting " +
          object_key +
          " from " +
          bucket_name +
          "/copy-destination folder"
      );
      const delete_object_from_folder_params = {
        Bucket: bucket_name,
        Key: "copy-destination/" + object_key,
      };

      await s3Client.send(
        new DeleteObjectCommand(delete_object_from_folder_params)
      );
      console.log("Success. Object deleted from folder.");
      try {
        console.log("\nDeleting the bucket named " + bucket_name + "...\n");
        const delete_bucket_params = { Bucket: bucket_name };
        await s3Client.send(new DeleteBucketCommand(delete_bucket_params));
        console.log("Success. First bucket deleted.");
      } catch (err) {
        console.log("Error deleting object from folder.", err);
      }
    } catch (err) {
      console.log("Error deleting  bucket.", err);
    }
  } catch (err) {
    console.log("Error deleting object from  bucket.", err);
  }
};
