/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example follows the steps in "Getting started with Amazon S3" in the Amazon S3
User Guide.
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html

Purpose:
Shows how to use the AWS SDK for JavaScript (v3) to get started using Amazon Simple Storage
Service (Amazon S3). Create a bucket, move objects into and out of it, and delete all
resources at the end of the demo.

Inputs (in command line):
node s3_basics.js <the bucket name> <the AWS Region to use> <object name> <object content>

Running the code:
node s3_basics.js
*/
// snippet-start:[s3_basics.JavaScript.s3_basics]
import {
  CreateBucketCommand,
  PutObjectCommand,
  CopyObjectCommand,
  DeleteObjectCommand,
  DeleteBucketCommand,
  GetObjectCommand
} from "@aws-sdk/client-s3";
import { s3Client, REGION } from "../libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

if (process.argv.length < 5) {
  console.log(
      "Usage: node s3_basics.js <the bucket name> <the AWS Region to use> <object name> <object content>\n" +
      "Example: node s3_basics_full.js test-bucket 'test.txt' 'Test Content'"
  );
}
const bucket_name = process.argv[2];
const object_key = process.argv[3];
const object_content = process.argv[4];

export const run = async (bucket_name, object_key, object_content) => {
  try {
    const create_bucket_params = {
      Bucket: bucket_name
    };
    console.log("\nCreating the bucket, named " + bucket_name + "...\n");
    console.log("about to create");
    const data = await s3Client.send(
        new CreateBucketCommand(create_bucket_params)
    );
    console.log("Bucket created at ", data.Location);
    try {
      console.log(
          "\nCreated and uploaded an object named " +
          object_key +
          " to first bucket " +
          bucket_name +
          " ...\n"
      );
      // Set the parameters for the object to upload.
      const object_upload_params = {
        Bucket: bucket_name,
        // Specify the name of the new object. For example, 'test.html'.
        // To create a directory for the object, use '/'. For example, 'myApp/package.json'.
        Key: object_key,
        // Content of the new object.
        Body: object_content,
      };
      // Create and upload the object to the first S3 bucket.
      await s3Client.send(new PutObjectCommand(object_upload_params));
      console.log(
          "Successfully uploaded object: " +
          object_upload_params.Bucket +
          "/" +
          object_upload_params.Key
      );
      try {
        const download_bucket_params = {
          Bucket: bucket_name,
          Key: object_key
        };
        console.log(
            "\nDownloading " +
            object_key +
            " from" +
            bucket_name +
            " ...\n"
        );
        // Create a helper function to convert a ReadableStream into a string.
        const streamToString = (stream) =>
            new Promise((resolve, reject) => {
              const chunks = [];
              stream.on("data", (chunk) => chunks.push(chunk));
              stream.on("error", reject);
              stream.on("end", () => resolve(Buffer.concat(chunks).toString("utf8")));
            });

        // Get the object from the Amazon S3 bucket. It is returned as a ReadableStream.
        const data = await s3Client.send(new GetObjectCommand(download_bucket_params));
        // Convert the ReadableStream to a string.
        const bodyContents = await streamToString(data.Body);
        console.log(bodyContents);
        try {
          // Copy the object from the first bucket to the second bucket.
          const copy_object_params = {
            Bucket: bucket_name,
            CopySource: "/" + bucket_name + "/" + object_key,
            Key: "copy-destination/" + object_key,
          };
          console.log(
              "\nCopying " +
              object_key +
              " from" +
              bucket_name +
              " to " +
              bucket_name +
              "/" +
              copy_object_params.Key +
              " ...\n"
          );
          await s3Client.send(new CopyObjectCommand(copy_object_params));
          console.log("Success, object copied to folder.");
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
                console.log(
                    "\nDeleting the bucket named " + bucket_name + "...\n"
                );
                const delete_bucket_params = {Bucket: bucket_name};
                await s3Client.send(
                    new DeleteBucketCommand(delete_bucket_params)
                );
                console.log("Success. First bucket deleted.");
                return "Run successfully"; // For unit tests.
              } catch (err) {
                console.log("Error deleting object from folder.", err);
                process.exit(1);
              }
            } catch (err) {
              console.log("Error deleting  bucket.", err);
              process.exit(1);
            }
          } catch (err) {
            console.log("Error deleting object from  bucket.", err);
            process.exit(1);
          }
        } catch (err) {
          console.log("Error copying object from to folder", err);
          process.exit(1);
        }
      } catch (err) {
        console.log("Error downloading object", err);
        process.exit(1);

      }
    }catch (err) {
      console.log("Error creating and upload object to  bucket", err);
      process.exit(1);
    }
    console.log("works");
  } catch (err) {
    console.log("Error creating bucket", err);
  }
};
run(bucket_name, object_key, object_content);

// snippet-end:[s3_basics.JavaScript.s3_basics]
