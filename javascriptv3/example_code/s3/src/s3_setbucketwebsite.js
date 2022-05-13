/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with the AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-static-web-host.html.

Purpose:
s3_setbucketwebsite.js applies a bucket website configuration to a selected bucket, and is part of an example
of using an Amazon S3 bucket as a static web host.

Inputs (replace in code):
- INDEX_PAGE
- ERROR_PAGE

Running the code:
nodes3_setbucketwebsite.js
 */
// snippet-start:[s3.JavaScript.website.putBucketWebsiteV3]
// Import required AWS SDK clients and commands for Node.js.
import { PutBucketWebsiteCommand } from "@aws-sdk/client-s3";
import { s3Client } from "./libs/s3Client.js"; // Helper function that creates an Amazon S3 service client module.

// Create the parameters for the bucket
export const bucketParams = { Bucket: "BUCKET_NAME" };
export const staticHostParams = {
  Bucket: bucketParams,
  WebsiteConfiguration: {
    ErrorDocument: {
      Key: "",
    },
    IndexDocument: {
      Suffix: "",
    },
  },
};

export const run = async () => {
  // Insert specified bucket name and index and error documents into parameters JSON
  // from command line arguments
  staticHostParams.Bucket = bucketParams;
  staticHostParams.WebsiteConfiguration.IndexDocument.Suffix = "INDEX_PAGE"; // The index document inserted into parameters JSON.
  staticHostParams.WebsiteConfiguration.ErrorDocument.Key = "ERROR_PAGE"; // The error document inserted into parameters JSON.
  // Set the new website configuration on the selected bucket.
  try {
    const data = await s3Client.send(new PutBucketWebsiteCommand(staticHostParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.website.putBucketWebsiteV3]
// For unit testing only.
// module.exports ={run, bucketParams, staticHostParams};
