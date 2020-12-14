/* Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
SPDX-License-Identifier: Apache-2.0

ABOUT THIS NODE.JS EXAMPLE: This sample is part of the SDK for JavaScript Developer Guide (scheduled for release September 2020) top
https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-example-configuring-buckets.html.

Purpose:
s3_setcors.js demonstrates how to set the CORS configuration of an Amazon S3 bucket.

Inputs:
- REGION (into command line below)
- BUCKET_NAME (into command line below)

Running the code:
node s3_setcors.js BUCKET_NAME REGION
 */
// snippet-start:[s3.JavaScript.v3.cors.putBucketCors]
async function run() {
  // Import required AWS-SDK clients and commands for Node.js
  const { S3Client, PutBucketCorsCommand } = require("@aws-sdk/client-s3");

  // Set the AWS region
  const REGION = "REGION";

  // Set params
  // Create initial parameters JSON for putBucketCors
  const thisConfig = {
    AllowedHeaders: ["Authorization"],
    AllowedMethods: [],
    AllowedOrigins: ["*"],
    ExposeHeaders: [],
    MaxAgeSeconds: 3000,
  };
  // Assemble the list of allowed methods based on command line parameters
  const allowedMethods = [];
  process.argv.forEach(function (val, index, array) {
    if (val.toUpperCase() === "POST") {
      allowedMethods.push("POST");
    }
    if (val.toUpperCase() === "GET") {
      allowedMethods.push("GET");
    }
    if (val.toUpperCase() === "PUT") {
      allowedMethods.push("PUT");
    }
    if (val.toUpperCase() === "PATCH") {
      allowedMethods.push("PATCH");
    }
    if (val.toUpperCase() === "DELETE") {
      allowedMethods.push("DELETE");
    }
    if (val.toUpperCase() === "HEAD") {
      allowedMethods.push("HEAD");
    }
  });

  // Copy the array of allowed methods into the config object
  thisConfig.AllowedMethods = allowedMethods;

  // Create array of configs then add the config object to it
  const corsRules = new Array(thisConfig);

  // Create CORS params
  const corsParams = {
    Bucket: "BUCKET_NAME",
    CORSConfiguration: { CORSRules: corsRules },
  };

  // Create S3 service object
  const s3 = new S3Client({ region: REGION });

  try {
    const data = await s3.send(new PutBucketCorsCommand(corsParams));
    console.log("Success", data);
  } catch (err) {
    console.log("Error", err);
  }
}
run();
// snippet-end:[s3.JavaScript.v3.cors.putBucketCors]
exports.run = run; //for unit tests only
