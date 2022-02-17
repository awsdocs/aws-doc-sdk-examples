/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-creating-buckets.html.

Purpose:
s3_multipartupload.js demonstrates how to upload a single object to an Amazon Simple Storage Solution (S3) bucket
 as a set of parts.
 For more information about multipart uploads, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/mpuoverview.html.

Inputs (replace in code):
- BUCKET_NAME
- LARGE_FILE_NAME
- NUMBER_OF_PARTS - between 1 and 10000. Enter without surrounding inverted commas.

Running the code:
nodes3_createbucket.js
*/
// snippet-start:[s3.JavaScript.buckets.multipartupload_v3]

// Load the required clients and commands.
import {
  CreateMultipartUploadCommand,
  UploadPartCommand,
  CompleteMultipartUploadCommand }
from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

// Set the parameters.
export const createParams = {
  Bucket: "BUCKET",
  Key: "LARGE_FILE_NAME",
};

// Specify how many parts in the upload. Between 1 and 10000.
const parts = NUMBER_OF_PARTS; // For example, 3.

export const run = async () => {
  try {
    // Create the mutlipart upload.
    const data = await s3Client.send(
      new CreateMultipartUploadCommand(createParams)
    );
    console.log("Upload started. Upload ID: ", data.UploadId);
    return data
    // Use loop to run UploadPartCommand for each part.
    var i;
    for (i = 0; i < parts; i++) {
      var uploadParams = {
        Bucket: createParams.Bucket,
        Key: createParams.Key,
        PartNumber: i,
        UploadId: data.UploadId,
      };
      try {
        const data = await s3Client.send(new UploadPartCommand(uploadParams));
        console.log("Part uploaded. ETag: ", data.ETag);
        return data; // For unit tests.
        var completeParams = {
          Bucket: createParams.Bucket,
          Key: createParams.Key,
          MultipartUpload: {
            Parts: [
              {
                ETag: data.ETag,
                PartNumber: i,
              },
            ],
          },
          UploadId: uploadParams.UploadId,
        };
      } catch (err) {
        console.log("Error uploading part", err);
      }
    }
  } catch (err) {
    console.log("Error creating upload ", err);
  }
  try {
    // Complete the mutlipart upload.
    const data = await s3Client.send(
      new CompleteMultipartUploadCommand(completeParams)
    );
    console.log("Upload completed. File location: ", data.Location);
    return data; // For unit tests.

  } catch (err) {
    console.log("Error ", err);
  }
};
run();
// snippet-end:[s3.JavaScript.buckets.multipartupload_v3]
// For unit testing only.
// module.exports ={run, createParams, completeParams};
