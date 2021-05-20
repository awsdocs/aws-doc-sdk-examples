/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0
ABOUT THIS NODE.JS EXAMPLE: This example works with AWS SDK for JavaScript version 3 (v3),
which is available at https://github.com/aws/aws-sdk-js-v3. This example is in the 'AWS SDK for JavaScript v3 Developer Guide' at
// https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-configuring-buckets.html.

Purpose:
s3_getcors.js demonstrates how to retrieve the CORS configuration of an Amazon S3 bucket.

Inputs :
- BUCKET_NAME

Running the code:
nodes3_getcors.js
 */
// snippet-start:[s3.JavaScript.cors.getBucketCorsV3]

// Import required AWS SDK clients and commands for Node.js
import { GetBucketCorsCommand } from "@aws-sdk/client-s3";
 import { s3 } from "./libs/s3Client.js"; // Helper function that creates Amazon S3 service client module.

// Create the parameters for calling
const bucketParams = { Bucket: "BUCKET_NAME" };

const run = async () => {
  try {
    const data = await s3.send(new GetBucketCorsCommand(bucketParams));
    console.log("Success", JSON.stringify(data.CORSRules));
    return data;
  } catch (err) {
    console.log("Error", err);
  }
};
run();
// snippet-end:[s3.JavaScript.cors.getBucketCorsV3]
// For unit testing only.
// module.exports ={run, bucketParams};
